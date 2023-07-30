package ru.practicum.shareit.booking.dto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

import lombok.*;

//@Getter
//@Setter
//@AllArgsConstructor
@Data
@Builder
public class BookingInfoDto {

    private Long id;

    private Long bookerId;

    @FutureOrPresent
    private LocalDateTime start;

    @Future
    private LocalDateTime end;
}