package com.baffang;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "jwt.secret=test-only-secret-key-0123456789abcdef0123456789")
class BaffangApplicationTests {

    @Test
    void contextLoads() {
    }

}
