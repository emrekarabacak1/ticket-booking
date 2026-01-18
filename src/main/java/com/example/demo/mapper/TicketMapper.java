package com.example.demo.mapper;

import com.example.demo.dto.TicketResponseDto;
import com.example.demo.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(source = "event.name", target = "eventName")
    @Mapping(source = "seat.row", target = "seatRow")
    @Mapping(source = "seat.number", target = "seatNumber")
    TicketResponseDto toResponseDto(Ticket ticket);

}
