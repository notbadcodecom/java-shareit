package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.RequestParameters;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(ItemRequestDto itemRequestDto, Long requesterId) {
        return post("", requesterId, itemRequestDto);
    }

    public ResponseEntity<Object> getItemRequestsByRequesterId(Long requesterId) {
        return get("", requesterId);
    }

    public ResponseEntity<Object> getAllRequests(int from, int size, Long requesterId) {
        return get("/all?from={from}&size={size}", requesterId, RequestParameters.of(from, size));
    }

    public ResponseEntity<Object> getRequestById(Long requestId, Long requesterId) {
        return get("/" + requestId, requesterId);
    }
}
