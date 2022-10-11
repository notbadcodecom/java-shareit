package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;

    private String authorName;

    @NotNull
    private String text;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime created;
}
