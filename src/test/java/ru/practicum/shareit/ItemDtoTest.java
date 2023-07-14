package ru.practicum.shareit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    private final ItemDto itemDto = new ItemDto(
            1L,
            "item",
            "description",
            false,
            1L
            );

    @Test
    void itemDto() throws Exception {
        var res = json.write(itemDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.name");
        assertThat(res).hasJsonPath("$.description");
        assertThat(res).hasJsonPath("$.available");
        assertThat(res).hasJsonPath("$.request");

        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(res).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(res).extractingJsonPathBooleanValue("$.available").isEqualTo(false);

    }

}