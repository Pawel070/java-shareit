package ru.practicum.shareit.booking.dto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
public class BookingQueryDto {

    private Long itemId;

    @FutureOrPresent
    private LocalDateTime start;

    @Future
    private LocalDateTime end;
}
