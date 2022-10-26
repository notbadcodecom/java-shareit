package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto createRequest(
            @Valid @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader(name = "X-Sharer-User-Id") Long requesterId
    ) {
        log.info("POST /requests : {}", itemRequestDto);
        return itemRequestService.create(itemRequestDto, requesterId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getRequestsByRequesterId(
            @RequestHeader(name = "X-Sharer-User-Id") Long requesterId
    ) {
        log.info("GET /requests");
        return itemRequestService.getItemRequestsByRequesterId(requesterId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getAllRequests(
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestHeader(name = "X-Sharer-User-Id") Long requesterId
    ) {
        log.info("GET /requests/all?from={}&size={}", from, size);
        return itemRequestService.getAllRequests(from, size, requesterId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto getRequestById(
            @PathVariable Long requestId,
            @RequestHeader(name = "X-Sharer-User-Id") Long requesterId
    ) {
        log.info("GET /requests/{}", requestId);
        return itemRequestService.getRequestById(requestId, requesterId);
    }
}
