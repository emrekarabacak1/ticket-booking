package com.example.demo.controller;

import com.example.demo.dto.EventRequestDto;
import com.example.demo.dto.EventResponseDto;
import com.example.demo.entity.Category;
import com.example.demo.entity.Event;
import com.example.demo.entity.Seat;
import com.example.demo.security.RoleConstants;
import com.example.demo.service.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController{

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + RoleConstants.ADMIN + "')")
    public ResponseEntity<EventResponseDto> addEvent(@Valid @RequestBody EventRequestDto eventRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(eventRequestDto));
    }

    @GetMapping
    public ResponseEntity<Page<EventResponseDto>> getAllEvents(@PageableDefault(page = 0, size = 10, sort = "date") Pageable pageable){
        return ResponseEntity.ok(eventService.getAllEvents(pageable));
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<Page<EventResponseDto>> getEventsByCategory(@PathVariable Long id, @PageableDefault(page = 0, size = 10, sort = "date") Pageable pageable){
        return ResponseEntity.ok(eventService.getEventsByCategory(id,pageable));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponseDto>> findByDateAfter(){
        return ResponseEntity.ok(eventService.getUpComingEvents(LocalDateTime.now()));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<Seat>> getAvailableSeats(@PathVariable Long id){
        return ResponseEntity.ok(eventService.getAvailableSeats(id));
    }

}
