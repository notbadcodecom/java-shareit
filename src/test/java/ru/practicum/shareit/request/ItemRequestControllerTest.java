package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class ItemRequestControllerTest {
    ItemRequestController itemRequestController;
    ItemRequestService itemRequestService;
    ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestService = Mockito.mock(ItemRequestService.class);
        itemRequestController = new ItemRequestController(itemRequestService);
        itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .created(LocalDateTime.now().withNano(0))
                .build();
    }

    @Test
    @DisplayName("Create request")
    void createRequest() {
        // Assign
        Mockito.when(itemRequestService.create(
                    ArgumentMatchers.any(ItemRequestDto.class), ArgumentMatchers.anyLong())
                ).then(invocation -> {
                    ItemRequestDto itemRequestDto = invocation.getArgument(0);
                    itemRequestDto.setId(1L);
                    return itemRequestDto;
                });

        // Act
        ItemRequestDto testedItemRequestDto = itemRequestService.create(itemRequestDto, 1L);

        // Asserts
        assertThat(testedItemRequestDto).isNotNull();
        assertThat(testedItemRequestDto.getId()).isNotNull();
        assertThat(testedItemRequestDto.getDescription()).isEqualTo(itemRequestDto.getDescription());
        assertThat(testedItemRequestDto.getCreated()).isEqualTo(itemRequestDto.getCreated());
    }

    @Test
    void getRequestsByRequesterId() {
        // Assign
        Mockito.when(itemRequestService.getItemRequestsByRequesterId(ArgumentMatchers.anyLong()))
                .then(invocation -> {
                    itemRequestDto.setId(1L);
                    return Collections.singletonList(itemRequestDto);
                });

        // Act
        List<ItemRequestDto> testedItemRequestDtoList = itemRequestService.getItemRequestsByRequesterId(1L);

        // Asserts
        assertThat(testedItemRequestDtoList).isNotNull();
        assertThat(testedItemRequestDtoList.size()).isNotNull();
    }
}
