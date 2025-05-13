package hu.progtech.chat.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import hu.progtech.chat.config.AppConfig;
import hu.progtech.chat.config.TokenSettings;
import hu.progtech.chat.models.User;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TokenManager {
    private static final Logger LOGGER = LogManager.getLogger(TokenManager.class);

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final TokenSettings settings;

    public TokenManager(final TokenSettings settings) {
        if (settings == null) {
            LOGGER.error("TokenSettings cannot be null during TokenManager instantiation.");
            throw new IllegalArgumentException("TokenSettings cannot be null.");
        }
        this.settings = settings;
        this.algorithm = Algorithm.HMAC256(settings.secretKey());
        this.verifier = JWT.require(algorithm).withIssuer(settings.issuer()).build();
        LOGGER.info("TokenManager initialized with issuer: {}", settings.issuer());
    }

    public String generateToken(final User user) {
        if (user == null || user.id() == 0) {
            throw new IllegalArgumentException("User must be valid and persisted (have an ID) to generate a token.");
        }

        final Date now = new Date();

        LOGGER.info("Generating token for user ID: {}.", user.id());
        return JWT.create()
                .withIssuer(settings.issuer())
                .withSubject(Long.toString(user.id()))
                .withIssuedAt(now)
                .sign(algorithm);
    }

    public long validateTokenAndGetClaims(final String token) throws TokenValidationException {
        if (token == null || token.isBlank()) {
            throw new TokenValidationException("Token cannot be null or empty.");
        }

        try {
            final DecodedJWT decodedJwt = verifier.verify(token);
            final String subject = decodedJwt.getSubject();
            if (subject == null) {
                LOGGER.warn("JWT subject (user ID) is null in token.");
                throw new TokenValidationException("JWT subject is missing.");
            }

            return Long.parseLong(decodedJwt.getSubject());
        } catch (AlgorithmMismatchException e) {
            LOGGER.warn("JWT algorithm mismatch: {}.", e.getMessage());
            throw new IllegalArgumentException("JWT algorithm mismatch.", e);
        } catch (SignatureVerificationException e) {
            LOGGER.warn("JWT signature validation failed: {}", e.getMessage());
            throw new IllegalArgumentException("JWT signature validation failed.", e);
        } catch (InvalidClaimException e) {
            LOGGER.warn("JWT claims are invalid (e.g., issuer mismatch): {}.", e.getMessage());
            throw new IllegalArgumentException("JWT claims are invalid.", e);
        } catch (JWTVerificationException e) {
            LOGGER.warn("JWT verification failed: {}.", e.getMessage());
            throw new IllegalArgumentException("JWT verification failed.", e);
        } catch (NumberFormatException e) {
            LOGGER.warn("JWT subject is not a valid user ID (long): {}.", e.getMessage());
            throw new IllegalArgumentException("JWT subject is invalid.", e);
        }
    }
}
