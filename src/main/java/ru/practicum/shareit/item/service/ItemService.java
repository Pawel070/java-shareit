package ru.practicum.shareit.item.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long id);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);

    List<ItemInfoDto> getItemsByOwner(Long id, Pageable pageable);

    ItemInfoDto getItemById(Long id, Long userId);

    ItemDto update(ItemDto itemDto, Long ownerId, Long itemId);

    void delete(Long itemId, Long ownerId);

    void deleteItemsByOwner(Long ownderId);

    List<ItemDto> getAvailableItems(Long userId, String text, Pageable pageable);

    List<CommentDto> getCommentsByItemId(Long itemId);

    Item findItemById(Long id);

    void deleteItemsByUser(Long userId);

    boolean isCheckItemOwner(Long itemId, Long userId);

    void isCheckFromSize(Long from, Long size);

}