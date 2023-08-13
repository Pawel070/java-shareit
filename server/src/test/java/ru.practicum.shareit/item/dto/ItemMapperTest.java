package ru.practicum.shareit.item.dto;


import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import org.assertj.core.api.AssertionsForInterfaceTypes;

import ru.practicum.shareit.user.model.User;

@JsonTest
class ItemMapperTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws Exception {
        User owner = new User(2L, "owner", "owner@mail.ru");
        ItemDto itemDto = new ItemDto(1L, "item", "desc", true, owner, 2L);
        JsonContent<ItemDto> result = json.write(itemDto);
        AssertionsForInterfaceTypes.assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        AssertionsForInterfaceTypes.assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        AssertionsForInterfaceTypes.assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("desc");
        AssertionsForInterfaceTypes.assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        AssertionsForInterfaceTypes.assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
    }


}
