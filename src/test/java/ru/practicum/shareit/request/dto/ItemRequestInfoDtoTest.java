package ru.practicum.shareit.request.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

@JsonTest
class ItemRequestInfoDtoTest {

    @Autowired
    private JacksonTester<ItemRequestInfoDto> json;

    LocalDateTime created = LocalDateTime.of(2023, 4, 07, 10, 10, 10);

    private final ItemDto itemDto = new ItemDto(
            1L,
            "name1",
            "description1",
            true,
            null,
            0);

    private final ItemRequestInfoDto itemRequestInfoDto = new ItemRequestInfoDto(
            2L,
            "description2",
            created,
            null);

    @Test
    void itemRequestInfoDto() throws Exception {
        List<ItemDto> items = new ArrayList<>();
        items.add(itemDto);
        itemRequestInfoDto.setItems(items);

        var res = json.write(itemRequestInfoDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.description");
        assertThat(res).hasJsonPath("$.created");
        assertThat(res).hasJsonPath("$.items");

        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(res).extractingJsonPathStringValue("$.description").isEqualTo("description2");
        assertThat(res).extractingJsonPathStringValue("$.created").isEqualTo(created.toString());
        assertThat(res).extractingJsonPathArrayValue("$.items").isInstanceOf(ArrayList.class);

        assertThat(res).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.items[0].name").isEqualTo("name1");
        assertThat(res)
                .extractingJsonPathStringValue("$.items[0].description").isEqualTo("description1");
        assertThat(res).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(res).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(0);
    }
}