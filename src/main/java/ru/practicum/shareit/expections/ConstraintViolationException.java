package ru.practicum.shareit.expections;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@Data
public class ConstraintViolationException extends RuntimeException {  // http:400 Аргумент метода недействителен, возникло исключение
    private String parameter;

    public ConstraintViolationException(String parameter) {
        super(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR));
        log.info("Искомый объект не найден > {} ", parameter);
        this.parameter = parameter;
        log.error("ConstraintViolationException Error {} ", parameter);
    }
}

