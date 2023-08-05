package ru.practicum.shareit.item;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.service.MyConstants.USER_ID;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    ItemInfoDto itemInfoDto;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    User owner;
    UserDto ownerDto;
    ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        owner = new User(1L, "user", "mail@ya.ru");
        itemDto = new ItemDto(1L, "item", "des", true, owner, 0);
        itemInfoDto = new ItemInfoDto(1L, "item", "des", true, ownerDto, null,
                null, null, null);
    }

    @Test
    void createItem() throws Exception {
        when(itemService.create(any(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    void createItem_withEmptyName() throws Exception {
        itemDto.setName("");

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_withBlankName() throws Exception {
        itemDto.setName("         ");

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_withNullName() throws Exception {
        itemDto.setName(null);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_withEmptyDescription() throws Exception {
        itemDto.setDescription("");

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_withNullDescription() throws Exception {
        itemDto.setDescription(null);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        when(itemService.update(any(), anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{id}", itemDto.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    void getItem() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemInfoDto);

        mockMvc.perform(get("/items/{id}", itemDto.getId())
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemInfoDto)));
    }

    @Test
    void getItemsByUser() throws Exception {
        when(itemService.getItemsByOwner(anyLong(), any())).thenReturn(List.of(itemInfoDto));

        mockMvc.perform(get("/items")
                        .header(USER_ID, "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemInfoDto.getName()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemInfoDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].lastBooking",
                        is(itemInfoDto.getLastBooking()), BookingInfoDto.class))
                .andExpect(jsonPath("$[0].nextBooking",
                        is(itemInfoDto.getNextBooking()), BookingInfoDto.class))
                .andExpect(jsonPath("$[0].comments", is(itemInfoDto.getComments())));
    }

    @Test
    void getUsersAvailableItems() throws Exception {
        when(itemService.getAvailableItems(anyLong(), anyString(), any())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header(USER_ID, "1")
                        .param("text", "item")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void createComment() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "comment text", "author", LocalDateTime.now());
        when(itemService.createComment(any(), anyLong(), anyLong())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName()), String.class));
    }

}