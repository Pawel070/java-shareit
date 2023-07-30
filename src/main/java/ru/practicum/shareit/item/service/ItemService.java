package ru.practicum.shareit.item.service;

import java.util.List;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long id);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);

    List<ItemDto> getItemsByOwner(Long id);

    ItemInfoDto getItemById(Long id, Long userId);

    ItemDto update(ItemDto itemDto, Long ownerId, Long itemId);

    void delete(Long itemId, Long ownerId);

    void deleteItemsByOwner(Long ownderId);

 //   List<ItemDto> searchItemsByText(String text, Integer from, Integer size);

    List<ItemDto> getAvailableItems(Long userId, String text);

    List<CommentDto> getCommentsByItemId(Long itemId);

    Item findItemById(Long id);

    void deleteItemsByUser(Long userId);

//    boolean isCheckAvailableItem(Long itemId);

    boolean isCheckItemOwner(Long itemId, Long userId);

 //   List<ItemDto> getItemsBySearchQuery(String text);
}