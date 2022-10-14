package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.handler.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final BookingService bookingService;
    private final CommentRepository commentRepository;

    @Override
    public CommentDto create(CommentDto commentDto, Long itemId, Long authorId) {
        if (commentDto.getText().isBlank()) {
            throw new BadRequestException("empty comment");
        }
        Booking booking = bookingService.findApprovedOrNotAvailableError(authorId, itemId);
        if (booking.getStart().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("booking not completed");
        }
        Comment comment = commentRepository.save(
                CommentMapper.toComment(commentDto, booking.getBooker(), booking.getItem())
        );
        booking.getItem().addComment(comment);
        return CommentMapper.toCommentDto(comment);
    }
}
