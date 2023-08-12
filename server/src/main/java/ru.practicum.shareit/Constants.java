package ru.practicum.shareit;

import org.springframework.data.domain.Sort;

public final class Constants {

    public static final String USER_ID = "X-Sharer-User-Id";
    public static final Sort SORT_DESC = Sort.by(Sort.Direction.DESC, "end");
    public static final Sort SORT_ASC = Sort.by(Sort.Direction.ASC, "start");
    public static final Sort SORT = Sort.by(Sort.Direction.DESC, "start");

}
