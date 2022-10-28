package ru.practicum.shareit.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.handler.exception.BadRequestException;


public class FromSizeRequest extends PageRequest {
    private final long offset;

    private FromSizeRequest(int from, int size) {
        super(from / size, size, Sort.unsorted());
        offset = from;
    }

    public static FromSizeRequest of(int from, int size) {
        if (from < 0 || size < 1) {
            throw new BadRequestException("not positive value in pagination");
        }
        return new FromSizeRequest(from, size);
    }

    @Override
    public long getOffset() {
        return offset;
    }
}
