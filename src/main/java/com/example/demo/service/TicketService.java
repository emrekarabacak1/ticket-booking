package com.example.demo.service;

import com.example.demo.dto.TicketRequestDto;
import com.example.demo.dto.TicketResponseDto;
import com.example.demo.entity.*;
import com.example.demo.repository.SeatRepository;
import com.example.demo.repository.TicketRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository, SeatRepository seatRepository) {
        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TicketResponseDto buyTicket(TicketRequestDto ticketRequestDto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("Kullanıcı bulunamadı"));
        Seat seat = seatRepository.findById(ticketRequestDto.getSeatId()).orElseThrow(()->new IllegalArgumentException("Koltuk bulunamadı"));

        if(seat.getEvent().getDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Etkinlik süresi dolmuştur");
        }

        if(seat.getStatus() != SeatStatus.AVAILABLE){
            throw new IllegalStateException("Koltuk satılmıştır!");
        }

        try{
            seat.setStatus(SeatStatus.SOLD);
            seatRepository.saveAndFlush(seat);
        } catch (ObjectOptimisticLockingFailureException e){
            throw new IllegalStateException("Üzgünüz! Bu koltuk az önce başkası tarafından satıl alındı.");
        }


        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setSeat(seat);
        ticket.setPrice(seat.getPrice());
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setEvent(seat.getEvent());

        Ticket savedTicket = ticketRepository.save(ticket);

        return mapToDto(savedTicket);
    }

    public List<TicketResponseDto> getMyTickets(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("Kullanıcı bulunamadı"));

        List<Ticket> tickets = ticketRepository.findByUserId(user.getId());

        return tickets.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private TicketResponseDto mapToDto(Ticket ticket){
        return TicketResponseDto.builder().
                id(ticket.getId())
                .eventName(ticket.getEvent().getName())
                .seatRow(ticket.getSeat().getRow())
                .seatNumber(ticket.getSeat().getNumber())
                .price(ticket.getPrice())
                .purchaseDate(ticket.getPurchaseDate())
                .build();
    }
}
