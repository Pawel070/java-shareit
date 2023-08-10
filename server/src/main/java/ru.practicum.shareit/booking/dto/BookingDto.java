package ru.practicum.shareit.booking.dto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

import lombok.*;

import ru.practicum.shareit.booking.model.Status;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {

    private Long id;

    @NotNull(message = "Время начала бронирования не может быть нулевым.")
    @FutureOrPresent(message = "Время начала бронирования не должно быть в прошлом.")
    private LocalDateTime start;

    @NotNull(message = "Время окончания бронирования не может быть нулевым.")
    @Future(message = "Время окончания бронирования не должно быть в будущем.")
    private LocalDateTime end;

    @NotNull(message = "Время бронирования не может быть нулевым.")
    private Long itemId;

    private Long bookerId;

    private Status status;


}