package com.example.demo.service.strategy;

import com.example.demo.entity.Event;
import com.example.demo.entity.Seat;
import com.example.demo.entity.SeatStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StandartSeatGenerationStrategy implements SeatGenerationStrategy {

    @Override
    public List<Seat> generate(Event event, int rows, int seatsPerRow) {
        List<Seat> seats = new ArrayList<>();

        for(int i = 0; i < rows; i++){
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
}
