package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
class ShareItTests {
    private ResponseEntity<String> re = new ResponseEntity<>("", HttpStatus.NOT_FOUND);

    @Test
    void contextLoads() {
    }
}
