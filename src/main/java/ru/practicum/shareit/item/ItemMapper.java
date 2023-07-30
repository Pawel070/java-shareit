package ru.practicum.shareit.item;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "authorName", source = "author.name")
    CommentDto toCommentDto(Comment comment);

    Item toItem(ItemDto itemDto);

    ItemDto toItemDto(Item item);

    ItemDto toItemExtDto(Item item);

    ItemDto mapToItemDtoResponse(Item item);

    Item mapToItemFromItemDto(ItemDto itemDto);

    default Item updatedItem(ItemDto itemDto, Item item) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName() == null ? item.getName() : itemDto.getName())
                .description(itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription())
                .available(itemDto.getAvailable() == null ? item.getAvailable() : itemDto.getAvailable())
                .build();
    }

    default ItemInfoDto toItemInfoDto(Item item, BookingInfoDto lastBooking,
                                      BookingInfoDto nextBooking, List<CommentDto> comments) {
        return ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(toUserDto(item.getOwner()))
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    UserDto toUserDto(User owner);

}

