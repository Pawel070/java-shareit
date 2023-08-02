package ru.practicum.shareit.request.service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private ItemRequestRepository itemRequestRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private ItemMapper mapper;

    @Override
    public ItemRequestInfoDto createItemRequest(Long id, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ItemRequestServiceImpl createItemRequest : Пользователь с УИН " + id + " не существует."));
        ItemRequest itemRequest = mapper.toItemRequest(itemRequestDto, user);
        return mapper.toItemRequestInfoDto(itemRequestRepository.save(itemRequest), new ArrayList<>());
    }

    @Override
    public List<ItemRequestInfoDto> getUsersItemRequests(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("ItemRequestServiceImpl createItemRequest : Пользователь с УИН " + id + " не существует.");
        }
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(id);
        return requests.stream()
                .map(req -> mapper.toItemRequestInfoDto(req, itemRepository.findAllByRequest_IdOrderByIdDesc(req.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestInfoDto> getItemRequests(Long id, Pageable pageable) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("ItemRequestServiceImpl createItemRequest : Пользователь с УИН " + id + " не существует.");
        }
        List<ItemRequest> requests = itemRequestRepository.findRequestsWithoutOwner(id, pageable);
        return requests.stream()
                .map(request -> mapper.toItemRequestInfoDto(request,
                        itemRepository.findAllByRequest_IdOrderByIdDesc(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestInfoDto getItemRequestById(Long requestId, Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("ItemRequestServiceImpl createItemRequest : Пользователь с УИН " + id + " не существует.");
        }
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequestServiceImpl createItemRequest : Запрос на вещь с УИН" + requestId + " не существует."));
        return mapper.toItemRequestInfoDto(itemRequest, itemRepository.findAllByRequest_IdOrderByIdDesc(itemRequest.getId()));
    }

    @Override
    public void isCheckFromSize(Long from, Long size) {
        log.info("ItemServiceImpl isCheckFromSize: Проверка from {} и size {}", from, size);
        if (from < 0 || size < 1) {
            throw new ru.practicum.shareit.exceptions.EntityNotAvailable("Ошибочный параметр \"size\" или \"from\"");
        }
    }

}
