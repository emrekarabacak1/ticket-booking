package com.example.demo.service.strategy;

import com.example.demo.entity.Event;
import com.example.demo.entity.Seat;

import java.util.List;

public interface SeatGenerationStrategy {
    List<Seat> generate(Event event,int rows, int seatsPerRow);
}
