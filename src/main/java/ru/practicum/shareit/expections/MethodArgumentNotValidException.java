package ru.practicum.shareit.expections;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class MethodArgumentNotValidException extends RuntimeException {  // http:400 Аргумент метода недействителен, возникло исключение
    private String parameter;

    public MethodArgumentNotValidException(String parameter) {
        log.info("Искомый объект не найден > {}", parameter);
        this.parameter = parameter;
        log.error("MethodArgumentNotValidException Error {} ", parameter);

    }
}

