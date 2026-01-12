package com.example.demo.service;

import com.example.demo.dto.EventRequestDto;
import com.example.demo.dto.EventResponseDto;
import com.example.demo.entity.Category;
import com.example.demo.entity.Event;
import com.example.demo.entity.Seat;
import com.example.demo.entity.SeatStatus;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.SeatRepository;
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

    public EventService(EventRepository eventRepository, CategoryRepository categoryRepository, SeatRepository seatRepository) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.seatRepository = seatRepository;
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

        //Koltukları otomatik oluştur
        //Yardımcı metodu çağır, bize 100 tane koltuk listesi dönüyor.
        List<Seat> seats = generateSeats(event, eventRequestDto.getNumberOfRows(), eventRequestDto.getSeatsPerRow());

        event.setSeats(seats);

        Event savedEvent = eventRepository.save(event);

        return mapToDto(savedEvent);
    }

    //Helper Method (yardımcı metot)
    // Koltuk listesi üretir, vt'na yazmaz.
    private List<Seat> generateSeats(Event event, int row, int seatsPerRow){
        List<Seat> seats = new ArrayList<>();

        for(int i = 0; i < row; i++){
            char rowChar = (char) ('A' + i);

            //O sıradaki koltukları gezer.
            for(int j = 1; j <= seatsPerRow; j++){
                Seat seat = new Seat();
                seat.setRow(String.valueOf(rowChar));
                seat.setNumber((long) j);
                seat.setPrice(event.getPrice());
                seat.setStatus(SeatStatus.AVAILABLE);

                seat.setEvent(event);

                seats.add(seat);
            }
        }
        return seats;
    }

    public Page<EventResponseDto> getAllEvents(Pageable pageable){
        Page<Event> eventPage = eventRepository.findAll(pageable);

        return eventPage.map(this::mapToDto);
    }

    public List<EventResponseDto> getEventsByCategory(Long categoryId){
        return eventRepository.findByCategoryId(categoryId).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<EventResponseDto> getUpComingEvents(LocalDateTime date){
        return eventRepository.findByDateAfter(LocalDateTime.now()).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<Seat> getAvailableSeats(Long eventId){
        return seatRepository.findByEventIdAndStatus(eventId,SeatStatus.AVAILABLE);
    }

    private EventResponseDto mapToDto(Event event){
        return EventResponseDto.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .date(event.getDate())
                .location(event.getLocation())
                .price(event.getPrice())
                .categoryName(event.getCategory().getName())
                .build();
    }

}
