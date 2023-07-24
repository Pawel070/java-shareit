package ru.practicum.shareit.request;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

//    ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest);

 //   @Mapping(source = "request.id", target = "requestId")
//    ItemDataForRequestDto toItemDataForRequestDto(Item item);

//    RequestDtoResponseWithMD toRequestDtoResponseWithMD(ItemRequest itemRequest);

//    List<RequestDtoResponseWithMD> toRequestDtoResponseWithMD(List<ItemRequest> itemRequests);
}