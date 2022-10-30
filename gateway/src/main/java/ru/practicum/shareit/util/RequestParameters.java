package ru.practicum.shareit.util;


import lombok.experimental.UtilityClass;

import java.util.Map;


@UtilityClass
public class RequestParameters {
    public static Map<String, Object> of(int from, int size) {
        return Map.of(
                "from", from,
                "size", size
        );
    }

    public static Map<String, Object> ofState(int from, int size, String state) {
        return Map.of(
                "state", state,
                "from", from,
                "size", size
        );
    }

    public static Map<String, Object> ofText(int from, int size, String text) {
        return Map.of(
                "text", text,
                "from", from,
                "size", size
        );
    }
}
