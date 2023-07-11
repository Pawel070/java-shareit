package ru.practicum.shareit.item.service;

import java.util.List;

import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long ownerId);

    List<ItemDto> getItemsByOwner(Long ownderId);

    ItemDto getItemById(Long id);

    ItemDto update(ItemDto itemDto, Long ownerId, Long itemId);

    ItemDto delete(Long itemId, Long ownerId);

    void deleteItemsByOwner(Long ownderId);

    List<ItemDto> getItemsBySearchQuery(String text);

}