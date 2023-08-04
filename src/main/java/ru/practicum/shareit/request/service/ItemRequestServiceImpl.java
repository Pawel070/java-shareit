package ru.practicum.shareit.request.service;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.EntityCheck;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;

    @Autowired
    EntityCheck entityCheck;

    @Transactional
    @Override
    public ItemRequestInfoDto createItemRequest(Long id, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ItemRequestServiceImpl createItemRequest : Пользователь с УИН " + id + " не существует."));
        ItemRequest itemRequest = mapper.toItemRequest(itemRequestDto, user);
        return mapper.toItemRequestInfoDto(itemRequestRepository.save(itemRequest), new ArrayList<>());
    }

    @Override
    public List<ItemRequestInfoDto> getUsersItemRequests(Long id) {
        entityCheck.isCheckUserId(id);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(id);
        return requests.stream()
                .map(req -> mapper.toItemRequestInfoDto(req, itemRepository.findAllByRequest_IdOrderByIdDesc(req.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestInfoDto> getItemRequests(Long id, Pageable pageable) {
        entityCheck.isCheckUserId(id);
        List<ItemRequest> requests = itemRequestRepository.findRequestsWithoutOwner(id, pageable);
        return requests.stream()
                .map(request -> mapper.toItemRequestInfoDto(request,
                        itemRepository.findAllByRequest_IdOrderByIdDesc(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestInfoDto getItemRequestById(Long requestId, Long id) {
        entityCheck.isCheckUserId(id);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequestServiceImpl createItemRequest : Запрос на вещь с УИН" + requestId + " не существует."));
        return mapper.toItemRequestInfoDto(itemRequest, itemRepository.findAllByRequest_IdOrderByIdDesc(itemRequest.getId()));
    }

}
