package ru.practicum.shareit.item.storage;

import java.util.List;

import ru.practicum.shareit.item.model.Item;

public interface ItemStorage {

    Item create(Item item);

    Item update(Item item);

    Item delete(Long userId);

    List<Item> getItemsByOwner(Long ownerId);

    List<Item> getItemsBySearchQuery(String text);

    void deleteItemsByOwner(Long ownderId);

    Item getItemById(Long itemId);

}