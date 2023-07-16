package ru.practicum.shareit.item.storage;

import static java.util.stream.Collectors.toList;

import javax.validation.ValidationException;

import java.util.*;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.item.model.Item;

@Slf4j
@Data
@Component
public class InMemoryItemStorage implements ItemStorage {

    public Map<Long, Item> items;
    private Long currentId = 0L;

    public InMemoryItemStorage() {
        items = new HashMap<>();
    }

    @Override
    public Item create(Item item) {
        if (isValidItem(item)) {
            ++currentId;
            item.setId(currentId);
            item.setName(item.getName());
            item.setDescription(item.getDescription());
            items.put(item.getId(), item);
        }
        log.info("Новой вещи установлен УИН : {}, УИН владельца : {}", item.getId(), item.getOwner());
        return item;
    }

    @Override
    public Item update(Item item) {
        if (item.getId() == null) {
            throw new ValidationException("Нет аргумента.");
        }
        if (!items.containsKey(item.getId())) {
            throw new NotFoundException("Вещь с УИН " + item.getId() + " отсутствует.");
        }
        if (item.getName() == null) {
            item.setName(items.get(item.getId()).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(items.get(item.getId()).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(items.get(item.getId()).getAvailable());
        }
        if (isValidItem(item)) {
            items.put(item.getId(), item);
        }
        log.info("У вещи с УИН {} обновлен УИН пользователя : {}", item.getId(), item.getOwner());
        return item;
    }

    @Override
    public Item delete(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Нет аргумента.");
        }
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Вещь с УИН " + itemId + " отсутствует.");
        }
        return items.remove(itemId);
    }

    @Override
    public List<Item> getItemsByOwner(Long ownerId) {
        log.debug("InMemoryItemStorage --> getItemsByOwner");
        return new ArrayList<>(items.values().stream()
                .filter(item -> item.getOwner().equals(ownerId))
                .collect(toList()));
    }

    @Override
    public void deleteItemsByOwner(Long ownerId) {
        log.debug("InMemoryItemStorage --> deleteItemsByOwner");
        List<Long> deleteIds = new ArrayList<>(items.values().stream()
                .filter(item -> item.getOwner().equals(ownerId))
                .map(Item::getId)
                .collect(toList()));
        for (Long deleteId : deleteIds) {
            items.remove(deleteId);
        }
    }

    @Override
    public Item getItemById(Long itemId) {
        log.debug("InMemoryItemStorage --> getItemById");
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Вещь с УИН " + itemId + " отсутствует.");
        }
        log.info("Вещь с УИН {} существует.", itemId);
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsBySearchQuery(String text) {
        log.debug("InMemoryItemStorage --> getItemsBySearchQuery");
        List<Item> searchItems = new ArrayList<>();
        if (!text.isBlank()) {
            searchItems = items.values().stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getName().toLowerCase(Locale.ROOT).contains(text) ||
                            item.getDescription().toLowerCase(Locale.ROOT).contains(text))
                    .collect(toList());
        }
        return searchItems;
    }

    private boolean isValidItem(Item item) {
        if (item.getName().isEmpty() || item.getDescription().isEmpty() || item.getAvailable() == null) {
            throw new ValidationException("Указанная вещь имеет ошибочные параметры.");
        }
        return true;
    }
}
