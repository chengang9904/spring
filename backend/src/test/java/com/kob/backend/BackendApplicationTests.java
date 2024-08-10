package com.kob.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.SQLOutput;

@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("admin"));
        System.out.println(passwordEncoder.encode("password"));
        System.out.println(passwordEncoder.matches("chengang","$2a$10$IKXgY3BrfiBmEMS3MQTdUehLw941hmqxp7BlQ88vnQsAUwEmwZuh6"));
    }

}
