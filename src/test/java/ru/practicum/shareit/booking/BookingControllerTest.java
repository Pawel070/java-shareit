package ru.practicum.shareit.booking;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingModelDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    BookingModelDto bookingModelDto;
    BookingDto bookingDto;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    User user;
    User owner;
    Item item;
    UserDto userDto;
    ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user", "user@mail.ru");
        owner = new User(2L, "owner", "owner@mail.ru");
        item = new Item(1L, "item", "desc", true, owner, null);
        userDto = new UserDto(1L, "user", "user@mail.ru");
        itemDto = new ItemDto(1L, "item", "desc", true, owner, 0);
        bookingModelDto = new BookingModelDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                itemDto,
                userDto,
                Status.WAITING);
        bookingDto = new BookingDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                itemDto.getId(),
                userDto.getId(),
                Status.WAITING);
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(any(), anyLong())).thenReturn(bookingModelDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingModelDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingModelDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingModelDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingModelDto.getStatus().toString())));
    }

    @Test
    void createBooking_startInPast() throws Exception {
        bookingDto.setStart(LocalDateTime.now().minusDays(5));

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        bookingModelDto.setStatus(Status.APPROVED);
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingModelDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .header(USER_ID, 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingModelDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingModelDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingModelDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingModelDto.getStatus().toString())));
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingModelDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                        .header(USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingModelDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingModelDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingModelDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingModelDto.getStatus().toString())));
    }

    @Test
    void getAllBookingByUser() throws Exception {
        when(bookingService.getAllBookingByUser(anyLong(), any(), any())).thenReturn(List.of(bookingModelDto));

        mockMvc.perform(get("/bookings")
                        .header(USER_ID, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingModelDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingModelDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingModelDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingModelDto.getStatus().toString())));
    }

    @Test
    void getAllBookingByOwner() throws Exception {
        when(bookingService.getAllBookingByOwner(anyLong(), any(), any())).thenReturn(List.of(bookingModelDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingModelDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingModelDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingModelDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingModelDto.getStatus().toString())));
    }

}