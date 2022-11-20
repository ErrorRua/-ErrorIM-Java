package com.errorim;

import com.errorim.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ErrorImCoreApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(JwtUtil.JWT_TTL);
    }

}
