package ru.practicum.shareit.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
public class PaginationInfo {
    Integer offset;
    Integer limit;
    Sort sort;

    public PaginationInfo(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public PageRequest asPageRequest() {
        return sort == null ? PageRequest.of(offset / limit, limit)
                : PageRequest.of(offset / limit, limit, sort);
    }
}