package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingQueryDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.service.CheckEntity;
/*
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    private CheckEntity checker;

    @Autowired
    @Lazy
    public ItemMapper(CheckEntity checkConsistencyService) {
        checker = checkConsistencyService;
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public ItemDto toItemDto(Item item) {
        Long request;
        if (item.getRequest() != null) {
            request = item.getRequest();
        } else {
            request = null;
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                request,
                null,
                null,
                checker.getCommentsByItemId(item.getId()));
    }

    public ItemDto toItemExtDto(Item item) {
        Long request;
        if (item.getRequest() != null) {
            request = item.getRequest();
        } else {
            request = null;
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                request,
                checker.getBookingLast(item.getId()),
                checker.getBookingNext(item.getId()),
                checker.getCommentsByItemId(item.getId()));
    }

    public Item toItem(ItemDto itemDto, Long ownerId) {
        Long request;
        if (itemDto.getRequest() != null) {
            request = itemDto.getRequest();
        } else {
            request = null;
        }
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                request
        );
    }

 */

@Mapper
public interface ItemMapper {

    CommentDto toCommentDto(Comment comment);

    ItemDto toItemDto(Item item);

    ItemDto toItemExtDto(Item item);

    Item toItem(ItemDto itemDto, Long ownerId);
}