package ru.practicum.shareit.expections;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MethodArgumentNotValidException extends RuntimeException {

    private static final long serialVersionUID = -2927785002140137706L;  // http:400 Аргумент метода недействителен, возникло исключение

    public MethodArgumentNotValidException(String parameter) {
        log.error("MethodArgumentNotValidException Error: Искомый объект не найден > {} ", parameter);

    }
}

