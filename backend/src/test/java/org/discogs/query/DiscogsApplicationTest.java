package org.discogs.query;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class DiscogsApplicationTest {

    @Test
    void main_WithDefaultArgs_ThrowsNoException() {
        assertDoesNotThrow(() -> DiscogsApplication.main(new String[0]));
    }

}
