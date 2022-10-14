package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;

public interface CommentService {
    CommentDto create(CommentDto commentDto, Long itemId, Long authorId);
}
