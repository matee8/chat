package hu.progtech.chat.util;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PasswordHasherTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void hashPassword_shouldThrow_forNullOrBlankPassword(String blankPassword) {
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    PasswordHasher.hashPassword(blankPassword);
                });
    }
}
