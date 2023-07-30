package ru.practicum.shareit.expections;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;

@Slf4j
public class ConstraintViolationException extends RuntimeException {

    private static final long serialVersionUID = 2313064197681140478L;
    // http:400 Аргумент метода недействителен, возникло исключение

    public ConstraintViolationException(String parameter) {
        super(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR));
        log.error("ConstraintViolationException Error: Искомый объект не найден >{} ", parameter);
    }
}

