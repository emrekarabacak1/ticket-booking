package com.example.demo.controller;

import com.example.demo.dto.TicketRequestDto;
import com.example.demo.dto.TicketResponseDto;
import com.example.demo.entity.Ticket;
import com.example.demo.repository.TicketRepository;
import com.example.demo.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponseDto> buyTicket(@Valid @RequestBody TicketRequestDto ticketRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.buyTicket(ticketRequestDto));
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<List<TicketResponseDto>> getMyTickets(){
        return ResponseEntity.ok(ticketService.getMyTickets());
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserveSeat(@RequestParam Long seatId) {
        ticketService.reserveSeat(seatId);
        return ResponseEntity.ok("Koltuk başarıyla rezerve edildi! 10 dakika süreniz başladı.");
    }
}
