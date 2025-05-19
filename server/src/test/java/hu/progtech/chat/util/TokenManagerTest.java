package hu.progtech.chat.util;

import static org.junit.jupiter.api.Assertions.assertThrows;

import hu.progtech.chat.config.TokenSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class TokenManagerTest {
    private static final String TEST_SECRET_KEY = "test-secret-key";
    private static final String TEST_ISSUER = "test-issuer";

    private TokenManager tokenManager;

    @BeforeEach
    void setUp() {
        TokenSettings tokenSettings = new TokenSettings(TEST_SECRET_KEY, TEST_ISSUER);
        tokenManager = new TokenManager(tokenSettings);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void validateTokenAndGetClaims_shouldThrow_forNullOrBlankToken(String invalidToken) {
        assertThrows(
                TokenValidationException.class,
                () -> {
                    tokenManager.validateTokenAndGetClaims(invalidToken);
                });
    }
}
