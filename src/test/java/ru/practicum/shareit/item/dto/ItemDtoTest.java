package ru.practicum.shareit.item.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    private final ItemDto itemDto = new ItemDto(
            1L,
            "item",
            "description",
            false,
            new User(1L, "userName", "mail@mail.ru"),
            0);

    @Test
    void itemDto() throws Exception {
        var res = json.write(itemDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.name");
        assertThat(res).hasJsonPath("$.description");
        assertThat(res).hasJsonPath("$.available");
        assertThat(res).hasJsonPath("$.owner");
        assertThat(res).hasJsonPath("$.requestId");

        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(res).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(res).extractingJsonPathBooleanValue("$.available").isEqualTo(false);

        assertThat(res).extractingJsonPathNumberValue("$.owner.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.owner.name").isEqualTo("userName");
        assertThat(res).extractingJsonPathStringValue("$.owner.email").isEqualTo("mail@mail.ru");

        assertThat(res).extractingJsonPathArrayValue("$.request").isNull();
    }

}