package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;

import java.time.LocalDateTime;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.service.MyConstants.USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    ItemRequestInfoDto itemRequestInfoDto;
    ItemRequestDto itemRequestDto;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    User user;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user1", "user1@mail.ru");
        itemRequestInfoDto = new ItemRequestInfoDto(
                1L,
                "description1",
                LocalDateTime.of(2023, 4, 10, 10, 10, 10),
                null);
        itemRequestDto = new ItemRequestDto(
                itemRequestInfoDto.getId(),
                itemRequestInfoDto.getDescription(),
                null,
                LocalDateTime.of(2023, 4, 10, 10, 10, 10));
    }

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any()))
                .thenReturn(itemRequestInfoDto);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestInfoDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestInfoDto.getCreated().toString())));
    }

    @Test
    void createItemRequest_WithEmptyDescription() throws Exception {
        itemRequestDto.setDescription("");

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemRequests() throws Exception {
        when(itemRequestService.getUsersItemRequests(anyLong()))
                .thenReturn(List.of(itemRequestInfoDto));

        mockMvc.perform(get("/requests")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestInfoDto))));
    }

    @Test
    void getItemRequest() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(itemRequestInfoDto);

        mockMvc.perform(get("/requests/{requestId}", itemRequestDto.getId())
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestInfoDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestInfoDto.getCreated().toString())));
    }

    @Test
    void getAllRequest() throws Exception {
        when(itemRequestService.getItemRequests(anyLong(), any())).thenReturn(List.of(itemRequestInfoDto));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestInfoDto))))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class));
    }

    @Test
    void getAllRequest_withPagination() throws Exception {
        when(itemRequestService.getItemRequests(anyLong(), any()))
                .thenReturn(List.of(itemRequestInfoDto));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID, 1)
                        .param("from", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestInfoDto))))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class));
    }

    @Test
    void getAllRequest_withWrongFrom() throws Exception {
        when(itemRequestService.getItemRequests(anyLong(), any()))
                .thenReturn(List.of(itemRequestInfoDto));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID, 1)
                        .param("from", "-5")
                        .param("size", "5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllRequest_withWrongSize() throws Exception {
        when(itemRequestService.getItemRequests(anyLong(), any()))
                .thenReturn(List.of(itemRequestInfoDto));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID, 1)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

}