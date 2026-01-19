package com.example.demo.service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dto.NotificationMessage;
import com.example.demo.dto.TicketRequestDto;
import com.example.demo.dto.TicketResponseDto;
import com.example.demo.entity.*;
import com.example.demo.mapper.TicketMapper;
import com.example.demo.repository.SeatRepository;
import com.example.demo.repository.TicketRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;
    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    public TicketService(TicketRepository ticketRepository,
                         UserRepository userRepository,
                         SeatRepository seatRepository,
                         TicketMapper ticketMapper,
                         StringRedisTemplate redisTemplate,
                         RabbitTemplate rabbitTemplate) {
        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.ticketMapper = ticketMapper;
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    // Redis ile Geçici Rezervasyon (Sepete Ekle)
    public void reserveSeat(Long seatId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String key = "seatLock:" + seatId;

        Boolean isLocked = redisTemplate.opsForValue()
                .setIfAbsent(key, email, Duration.ofMinutes(10));

        if (Boolean.FALSE.equals(isLocked)) {
            throw new IllegalStateException("Bu koltuk şu an başkası tarafından rezerve edilmiş durumda! Lütfen 10 dakika sonra tekrar deneyin.");
        }
    }

    @Transactional
    public TicketResponseDto buyTicket(TicketRequestDto ticketRequestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long seatId = ticketRequestDto.getSeatId();

        // 1. Redis Kilidi Kontrolü
        String lockKey = "seatLock:" + seatId;
        String lockOwner = redisTemplate.opsForValue().get(lockKey);

        if (lockOwner != null && !lockOwner.equals(email)) {
            throw new IllegalStateException("Bu koltuk başkası tarafından rezerve edilmiş.");
        }

        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("Kullanıcı bulunamadı"));
        Seat seat = seatRepository.findById(seatId).orElseThrow(()->new IllegalArgumentException("Koltuk bulunamadı"));

        if(seat.getEvent().getDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Etkinlik süresi dolmuştur");
        }

        if(seat.getStatus() != SeatStatus.AVAILABLE){
            throw new IllegalStateException("Koltuk satılmıştır!");
        }

        try {
            seat.setStatus(SeatStatus.SOLD);
            seatRepository.saveAndFlush(seat);
        } catch (ObjectOptimisticLockingFailureException e){
            throw new IllegalStateException("Üzgünüz! Bu koltuk az önce başkası tarafından satın alındı.");
        }

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setSeat(seat);
        ticket.setPrice(seat.getPrice());
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setEvent(seat.getEvent());

        Ticket savedTicket = ticketRepository.save(ticket);

        // 2. Redis Kilidini Temizle (Artık DB'de satıldı)
        redisTemplate.delete(lockKey);

        // 3. RabbitMQ'ya Mesaj Gönder (ASENKRON)
        NotificationMessage message = new NotificationMessage(
                user.getEmail(),
                "Biletiniz Başarıyla Alındı!",
                "Sayın kullanıcımız, " + seat.getEvent().getName() + " etkinliği için biletiniz oluşturuldu. İyi eğlenceler!"
        );

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, message);

        System.out.println("✅ RabbitMQ Kuyruğuna mesaj bırakıldı: " + user.getEmail());

        return ticketMapper.toResponseDto(savedTicket);
    }

    public List<TicketResponseDto> getMyTickets(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("Kullanıcı bulunamadı"));

        List<Ticket> tickets = ticketRepository.findByUserId(user.getId());

        return tickets.stream().map(ticketMapper::toResponseDto).collect(Collectors.toList());
    }
}