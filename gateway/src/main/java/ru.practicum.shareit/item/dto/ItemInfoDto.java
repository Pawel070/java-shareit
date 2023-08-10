package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@Builder
@AllArgsConstructor
public class ItemInfoDto {
    private Long id;

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    private String description;

    @NotNull
    private Boolean available;

    private Long requestId;

    private UserDto owner;

    private List<CommentDto> comments;
}
