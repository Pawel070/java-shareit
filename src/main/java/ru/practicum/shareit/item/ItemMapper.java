package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.mapstruct.*;
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


@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "authorName", source = "author.name")
    CommentDto toCommentDto(Comment comment);

    Item toItem(ItemDto itemDto);

    ItemDto toItemDto(Item item);

    ItemDto toItemExtDto(Item item);


    // @Mapping(source = "request.id", target = "request")
    ItemDto mapToItemDtoResponse(Item item);

    Item mapToItemFromItemDto(ItemDto itemDto);
/*
    @Mapping(source = "booker.id", target = "booker")
    BookingShortDto mapToBookingShortDto(Booking booking);

    Comment mapToCommentFromCommentDto(CommentDto commentDto);

    @Mapping(source = "author.name", target = "authorName")
    CommentDtoResponse mapToCommentDtoResponseFromComment(Comment comment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item mapToItemFromItemDtoUpdate(ItemDtoUpdate itemDtoUpdate, @MappingTarget Item item);
    */


}

