package ru.practicum.shareit.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PaginationInfoTest {
    private final PaginationInfo info = new PaginationInfo(11, 5);

    @Test
    public void methodAsPageRequestCreatesPageRequestWithoutSort() {
        PageRequest request = info.asPageRequest();
        Assertions.assertInstanceOf(PageRequest.class, request);
        Assertions.assertEquals(2, request.getPageNumber());
        Assertions.assertEquals(10, request.getOffset());
        Assertions.assertEquals(5, request.getPageSize());
        Assertions.assertEquals(Sort.unsorted(), request.getSort());
    }

    @Test
    public void methodAsPageRequestCreatesPageRequestWithSort() {
        info.setSort(Sort.by(Sort.Direction.ASC, "property"));
        PageRequest request = info.asPageRequest();
        Assertions.assertInstanceOf(PageRequest.class, request);
        Assertions.assertEquals(2, request.getPageNumber());
        Assertions.assertEquals(10, request.getOffset());
        Assertions.assertEquals(5, request.getPageSize());
        Assertions.assertEquals(Sort.by(Sort.Direction.ASC, "property"), request.getSort());
        info.setSort(Sort.unsorted());
    }
}
