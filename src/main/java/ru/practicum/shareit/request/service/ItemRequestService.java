package ru.practicum.shareit.request.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

public interface ItemRequestService {

    ItemRequestInfoDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestInfoDto> getUsersItemRequests(Long userId);

    List<ItemRequestInfoDto> getItemRequests(Long userId, Pageable pageable);

    ItemRequestInfoDto getItemRequestById(Long requestId, Long userId);

    void isCheckFromSize(int from, int size);
}