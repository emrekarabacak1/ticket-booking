package com.example.demo.service;

import com.example.demo.dto.EventRequestDto;
import com.example.demo.dto.EventResponseDto;
import com.example.demo.entity.Category;
import com.example.demo.entity.Event;
import com.example.demo.entity.Seat;
import com.example.demo.entity.SeatStatus;
import com.example.demo.mapper.EventMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.SeatRepository;
import com.example.demo.service.strategy.SeatGenerationStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final SeatRepository seatRepository;
    private final SeatGenerationStrategy seatGenerationStrategy;
    private final EventMapper eventMapper;

    public EventService(EventRepository eventRepository, CategoryRepository categoryRepository, SeatRepository seatRepository, SeatGenerationStrategy seatGenerationStrategy, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.seatRepository = seatRepository;
        this.seatGenerationStrategy = seatGenerationStrategy;
        this.eventMapper = eventMapper;
    }

    @Transactional
    public EventResponseDto createEvent(EventRequestDto eventRequestDto){
        Category category = categoryRepository.findById(eventRequestDto.getCategoryId()).orElseThrow(()->new IllegalArgumentException("Kategori bulunamadı"));

        Event event = new Event();
        event.setName(eventRequestDto.getName());
        event.setDescription(eventRequestDto.getDescription());
        event.setLocation(eventRequestDto.getLocation());
        event.setPrice(eventRequestDto.getPrice());
        event.setDate(eventRequestDto.getDate());
        event.setCategory(category);

        List<Seat> seats = seatGenerationStrategy.generate(event, eventRequestDto.getNumberOfRows(), eventRequestDto.getSeatsPerRow());

        event.setSeats(seats);

        Event savedEvent = eventRepository.save(event);

        return eventMapper.toResponseDto(savedEvent);
    }

    public Page<EventResponseDto> getAllEvents(Pageable pageable){
        Page<Event> eventPage = eventRepository.findAll(pageable);

        return eventPage.map(eventMapper::toResponseDto);
    }

    public Page<EventResponseDto> getEventsByCategory(Long categoryId, Pageable pageable){
        Page<Event> eventPage = eventRepository.findByCategoryId(categoryId, pageable);

        return eventPage.map(eventMapper::toResponseDto);
    }

    public List<EventResponseDto> getUpComingEvents(LocalDateTime date){
        return eventRepository.findByDateAfter(LocalDateTime.now()).stream().map(eventMapper::toResponseDto).collect(Collectors.toList());
    }

    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Etkinlik bulunamadı id: " + id));

        eventRepository.delete(event);
    }

    public List<Seat> getAvailableSeats(Long eventId){
        return seatRepository.findByEventIdAndStatus(eventId,SeatStatus.AVAILABLE);
    }

}
