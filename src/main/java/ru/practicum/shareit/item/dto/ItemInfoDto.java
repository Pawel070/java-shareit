package ru.practicum.shareit.item.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@Builder
public class ItemInfoDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private UserDto owner;
    private Long requestId;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;
    private List<CommentDto> comments;
}