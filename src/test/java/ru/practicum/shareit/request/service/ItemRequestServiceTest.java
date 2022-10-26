package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.FromSizeRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestServiceTest {
    private ItemRequestRepository itemRequestRepository;
    private ItemRequestService itemRequestService;
    private User requester;
    private ItemRequest request;


    @BeforeEach
    void setUp() {
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        UserService userService = Mockito.mock(UserService.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService);
        requester = User.builder().id(1L).build();
        request = ItemRequest.builder()
                .description("item description")
                .build();
        Mockito.when(itemRequestRepository.save(ArgumentMatchers.any()))
                .then(invocation -> {
                    ItemRequest repoRequest = invocation.getArgument(0);
                    repoRequest.setId(99L);
                    return repoRequest;
                });
        Mockito.when(userService.getByIdOrNotFoundError(requester.getId()))
                .thenReturn(requester);
    }

    @Test
    @DisplayName("Create new itemRequest")
    void create() {
        // Assign
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);

        // Act
        ItemRequestDto itemRequestDto = itemRequestService.create(
                requestDto, requester.getId()
        );

        // Asserts
        assertThat(itemRequestDto).isNotNull();
        assertThat(itemRequestDto.getId()).isNotNull();
        assertThat(itemRequestDto.getDescription()).isEqualTo(request.getDescription());
    }

    @Test
    void getItemRequestsByRequesterId() {
    }

    @Test
    @DisplayName("Get all requests (if requester not owner)")
    void getAllRequestsIfRequesterNotOwner() {
        // Assign
        Mockito.when(itemRequestRepository.findByRequester_IdOrderByCreatedDesc(requester.getId()))
                .then(invocation -> new ArrayList<>());

        // Act
        List<ItemRequestDto> requests = itemRequestService.getItemRequestsByRequesterId(requester.getId());

        // Asserts
        assertThat(requests).isNotNull();
    }

    @Test
    @DisplayName("Get all requests")
    void getAllRequests() {
        // Assign
        Mockito.when(itemRequestRepository.findByRequester_IdNotOrderByCreatedDesc(
                requester.getId(), FromSizeRequest.of(0,20))
        ).then(invocation -> new ArrayList<>());

        // Act
        List<ItemRequestDto> requests = itemRequestService.getAllRequests(0, 20, requester.getId());

        // Asserts
        assertThat(requests).isNotNull();
    }

    @Test
    @DisplayName("Get request by id")
    void getRequestById() {
        // Assign
        Mockito.when(itemRequestRepository.findById(ArgumentMatchers.any()))
                .then(invocation -> {
                    Long id = invocation.getArgument(0);
                    request.setId(id);
                    return Optional.of(request);
                });

        // Act
        ItemRequestDto testedRequest = itemRequestService.getRequestById(
                request.getId(), requester.getId()
        );

        // Asserts
        assertThat(testedRequest).isNotNull();
        assertThat(testedRequest.getId()).isEqualTo(request.getId());
    }
}