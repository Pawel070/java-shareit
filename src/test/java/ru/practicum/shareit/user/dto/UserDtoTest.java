package ru.practicum.shareit.user.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.annotation.Rollback;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    @Rollback(false)
    void userDto() throws Exception {
        UserDto userDto = new UserDto(1L, "user", "user@email.ru");

        JsonContent<UserDto> res = json.write(userDto);

        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(res).extractingJsonPathStringValue("$.email").isEqualTo("user@email.ru");
    }

}