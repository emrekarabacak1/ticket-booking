package com.example.demo.service;

import com.example.demo.dto.TicketRequestDto;
import com.example.demo.dto.TicketResponseDto;
import com.example.demo.entity.*;
import com.example.demo.repository.SeatRepository;
import com.example.demo.repository.TicketRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TicketService ticketService;

    // Testlerde kullanacağımız sahte nesneler
    private User mockUser;
    private Seat mockSeat;
    private Event mockEvent;

    @BeforeEach
    void setUp() {
        // Her testten önce verileri sıfırla
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("ali@test.com");

        mockEvent = new Event();
        mockEvent.setName("Tarkan Konseri");

        mockSeat = new Seat();
        mockSeat.setId(10L);
        mockSeat.setRow("A");
        // DİKKAT: Long olduğu için sonuna L koyduk
        mockSeat.setNumber(5L);
        mockSeat.setPrice(new BigDecimal("500"));
        mockSeat.setStatus(SeatStatus.AVAILABLE); // Varsayılan boş
        mockSeat.setEvent(mockEvent);
    }

    // --- SENARYO 1: BAŞARILI SATIŞ ---
    @Test
    void buyTicket_ShouldSuccess_WhenSeatIsAvailable() {
        // --- ARRANGE ---

        // 1. Security Context Mocklama (Kullanıcı giriş yapmış gibi davran)
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("ali@test.com");
        SecurityContextHolder.setContext(securityContext);

        // 2. Veritabanı cevaplarını öğret
        when(userRepository.findByEmail("ali@test.com")).thenReturn(Optional.of(mockUser));
        when(seatRepository.findById(10L)).thenReturn(Optional.of(mockSeat));

        // Kayıt edileni geri dön
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // İstek oluştur
        TicketRequestDto request = new TicketRequestDto();
        request.setSeatId(10L);

        // --- ACT ---
        TicketResponseDto response = ticketService.buyTicket(request);

        // --- ASSERT ---
        assertNotNull(response);
        assertEquals("Tarkan Konseri", response.getEventName());
        assertEquals(5L, response.getSeatNumber());

        // Koltuk durumu SOLD oldu mu?
        assertEquals(SeatStatus.SOLD, mockSeat.getStatus());

        // Kayıt metodu 1 kere çağrıldı mı?
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    // --- SENARYO 2: BAŞARISIZ SATIŞ (Koltuk Dolu) ---
    @Test
    void buyTicket_ShouldThrowException_WhenSeatIsSold() {
        // --- ARRANGE ---

        // 1. Koltuğu bilerek "SATILMIŞ" yapıyoruz
        mockSeat.setStatus(SeatStatus.SOLD);

        // 2. Repository cevapları
        when(userRepository.findByEmail("ali@test.com")).thenReturn(Optional.of(mockUser));
        when(seatRepository.findById(10L)).thenReturn(Optional.of(mockSeat));

        // 3. Security
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("ali@test.com");
        SecurityContextHolder.setContext(securityContext);

        // İstek
        TicketRequestDto request = new TicketRequestDto();
        request.setSeatId(10L);

        // --- ACT & ASSERT ---
        // Hata fırlatmasını bekle
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.buyTicket(request);
        });

        // Hata mesajını kontrol et
        // (Servisindeki mesaj neyse aynısı olmalı: "Koltuk zaten satılmış" veya "Seat is already sold")
        assertEquals("Koltuk satılmıştır!", exception.getMessage());

        // Veritabanına kayıt atılmadığından emin ol
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}