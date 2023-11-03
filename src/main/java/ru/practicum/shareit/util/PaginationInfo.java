package ru.practicum.shareit.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
@Validated
@AllArgsConstructor
public class PaginationInfo {
    @NotNull
    @PositiveOrZero
    Integer offset;
    @NotNull
    @Positive
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