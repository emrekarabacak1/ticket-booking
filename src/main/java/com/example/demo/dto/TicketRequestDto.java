package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TicketRequestDto {
    @NotNull(message = "Koltuk ID (Seat ID) seçilmelidir")
    private Long seatId;
}
