package ru.practicum.shareit.itemRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;

import java.util.Map;

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

    public ResponseEntity<Object> findAllByRequester(long requesterId) {
        return get("", requesterId);
    }

    public ResponseEntity<Object> retrieve(long requestId, long userId) {
        return get(String.format("/%d", requestId), userId);
    }

    public ResponseEntity<Object> findAll(long seekerId, int from, int size) {
        Map<String, Object> params = Map.of("from", from, "size", size);
        return get("/all?from={from}&size={size}", seekerId, params);
    }

    public ResponseEntity<Object> create(long requesterId, ItemRequestDto dto) {
        return post("", requesterId, dto);
    }
}
