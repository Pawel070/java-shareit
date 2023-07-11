package ru.practicum.shareit.item.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.expections.ItemNotFoundException;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private ItemStorage itemStorage;
    private ItemMapper mapper;

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        log.info("Получен POST-запрос на создание вещи");
        return mapper.toItemDto(itemStorage.create(mapper.toItem(itemDto, ownerId)));
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownderId) {
        log.info("Получен GET-запрос на получение всех вещей владельца с УИН {}", ownderId);
        return itemStorage.getItemsByOwner(ownderId).stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }

    @Override
    public ItemDto getItemById(Long id) {
        log.info("Получен GET-запрос на получение вещи с УИН {}", id);
        return mapper.toItemDto(itemStorage.getItemById(id));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long ownerId, Long itemId) {
        log.info("Получен PUT-запрос на обновление вещи с УИН {}", itemId);
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        Item oldItem = itemStorage.getItemById(itemId);
        if (!oldItem.getOwner().equals(ownerId)) {
            throw new ItemNotFoundException("У пользователя нет такой вещи!");
        }
        return mapper.toItemDto(itemStorage.update(mapper.toItem(itemDto, ownerId)));
    }

    @Override
    public ItemDto delete(Long itemId, Long ownerId) {
        log.info("Получен DELETE-запрос на удаление вещи с УИН {}", itemId);
        Item item = itemStorage.getItemById(itemId);
        if (!item.getOwner().equals(ownerId)) {
            throw new ItemNotFoundException("У пользователя нет такой вещи!");
        }
        return mapper.toItemDto(itemStorage.delete(itemId));
    }

    @Override
    public void deleteItemsByOwner(Long ownderId) {
        log.info("Получен DELETE-запрос на удаление всех вещеепользователя с УИН {}", ownderId);
        itemStorage.deleteItemsByOwner(ownderId);
    }

    @Override
    public List<ItemDto> getItemsBySearchQuery(String text) {
        log.info("Получен GET-запрос на получение вещей по тексту={}", text);
        return itemStorage.getItemsBySearchQuery(text).stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }
}
