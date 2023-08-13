package ru.practicum.shareit.item;

import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

//@Mapper(componentModel = "spring", uses = ItemMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

//    @Mapping(target = "authorName", source = "author.name")
//    CommentDto toCommentDto(Comment comment);

    public static Item toItem(ItemDto itemDto, User owner, ItemRequest request) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .request(request)
                .build();
    }

    public static ItemDto toItemDto(Item item) { // преобразование Итема в ДТО
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item updatedItem(ItemDto itemDto, Item item) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName() == null ? item.getName() : itemDto.getName())
                .description(itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription())
                .available(itemDto.getAvailable() == null ? item.getAvailable() : itemDto.getAvailable())
                .build();
    }

    public static ItemInfoDto toItemInfoDto(Item item, BookingInfoDto lastBooking,
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

    public static UserDto toUserDto(User owner) {
        return UserDto.builder()
                .id(owner.getId())
                .name(owner.getName())
                .email(owner.getEmail())
                .build();
    }

    //   ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

    public static ItemRequestInfoDto toItemRequestInfoDto(ItemRequest itemRequest, List<Item> items) {
        return ItemRequestInfoDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList()))
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto request, User requester) {
        return ItemRequest.builder()
                .description(request.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }

}

