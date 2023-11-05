package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> findAllByOwner(long ownerId, int from, int size) {
        Map<String, Object> params = Map.of("from", from, "size", size);
        return get("?from={from}&size={size}", ownerId, params);
    }

    public ResponseEntity<Object> retrieve(long id, long requesterId) {
        return get(String.format("/%d", id), requesterId);
    }

    public ResponseEntity<Object> searchByText(String text, int from, int size) {
        Map<String, Object> params = Map.of("from", from, "size", size, "text", text);

        return get("/search?text={text}&from={from}&size={size}", null, params);
    }

    public ResponseEntity<Object> create(long ownerId, ItemDto itemDto) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> create(long itemId, long authorId, CommentDto dto) {
        return post(String.format("/%d/comment", itemId), authorId, dto);
    }

    public ResponseEntity<Object> update(long id, long ownerId, ItemDto itemDto) {
        return patch(String.format("/%d", id), ownerId, itemDto);
    }

    public ResponseEntity<Object> delete(long id) {
        return delete(String.format("/%d", id));
    }
}
