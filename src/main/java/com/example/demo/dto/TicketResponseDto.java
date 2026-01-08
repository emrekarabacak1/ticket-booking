package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TicketResponseDto {
    private Long id;
    private String eventName;
    private String seatRow;
    private Long seatNumber;
    private BigDecimal price;
    private LocalDateTime purchaseDate;
}