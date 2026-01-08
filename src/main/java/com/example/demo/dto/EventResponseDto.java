package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EventResponseDto {
    private Long id;
    private String name;
    private String description;
    private String location;
    private LocalDateTime date;
    private BigDecimal price;
    private String categoryName;
}
