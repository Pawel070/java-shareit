package ru.practicum.shareit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void itemRequestDtoTest() throws Exception {

        LocalDateTime created = LocalDateTime.of(2023, 4, 10, 10, 10, 10);

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "description1",
                "requester1",
                created);

        var res = json.write(itemRequestDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.description");
        assertThat(res).hasJsonPath("$.requester");
        assertThat(res).hasJsonPath("$.created");

        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.description").isEqualTo("description1");
        assertThat(res).extractingJsonPathStringValue("$.requester").isEqualTo("requester1");
        assertThat(res).extractingJsonPathStringValue("$.created").isEqualTo(created.toString());
    }
}