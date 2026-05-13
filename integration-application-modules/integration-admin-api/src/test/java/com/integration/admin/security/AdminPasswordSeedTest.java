package com.integration.admin.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminPasswordSeedTest {

    @Test
    void seedPasswordMatchesAdmin123() {
        String hash = "$2a$10$nVyiKo3TNs2FkYimpdBj4usIbrVRG3.7oe74j/bwrXUpQsTXG7IPG";
        assertTrue(new BCryptPasswordEncoder().matches("admin123", hash));
    }
}
