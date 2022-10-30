package ru.practicum.shareit.item.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;


@Getter
public class CommentDto {
    @NotNull
    private String text;
}
