package com.example.demo.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class EventRequestDto {

    @NotBlank(message = "Etkinlik adı zorunludur")
    private String name;

    private String description;

    @NotNull(message = "Etkinlik tarihi boş olamaz")
    @Future(message = "Etkinlik tarihi bugünden sonra olmalıdır")
    private LocalDateTime date;

    @NotBlank(message = "Konum bilgisi zorunludur")
    private String location;

    @NotNull
    @Min(value = 0, message = "Fiyat negatif olamaz")
    private BigDecimal price;

    @NotNull(message = "Kategori seçilmelidir")
    private Long categoryId;

    @Min(value = 1, message = "En az 1 sıra olmalıdır")
    private int numberOfRows;   // (A, B, C, D, E)

    @Min(value = 1, message = "Sıra başında en az 1 koltuk olmalıdır")
    private int seatsPerRow;    // (1, 2, 3, 4, 5, ...)
}
