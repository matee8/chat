package hu.progtech.chat.util;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.progtech.chat.config.AppConfig;
import hu.progtech.chat.config.TokenSettings;
import hu.progtech.chat.models.User;

public class TokenManager {
    private static final Logger logger = LogManager.getLogger(TokenManager.class);
    private static TokenManager instance;

    private final Algorithm algorithm;
    private final String issuer;
    private final JWTVerifier verifier;

    private TokenManager(TokenSettings settings) {
         this.algorithm = Algorithm.HMAC256(settings.getSecretKey());
         this.issuer = settings.getIssuer();
         this.verifier = JWT.require(algorithm)
             .withIssuer(issuer)
             .build();
    }

    public String generateToken(User user) {
        Date now = new Date();

        return JWT.create()
            .withIssuer(issuer)
            .withSubject(Long.toString(user.getId()))
            .withIssuedAt(now)
            .sign(algorithm);
    }

    public long validateTokenAndGetClaims(String token) {
        try {
            DecodedJWT decodedJwt = verifier.verify(token);

            System.out.println(decodedJwt.getSubject());
            return Long.parseLong(decodedJwt.getSubject());
        } catch (AlgorithmMismatchException e) {
            logger.warn("JWT algorithm mismatch: " + e.getMessage());
        } catch (SignatureVerificationException e) {
            logger.warn("JWT signature validation failed: " + e.getMessage());
        } catch (InvalidClaimException e) {
            logger.warn("JWT claims are invalid (e.g., issuer mismatch) " + e.getMessage());
        } catch (JWTVerificationException e) {
            logger.warn("JWT verification failed: {}", e.getMessage());
        }

        throw new IllegalArgumentException("Token verification failed.");
    }

    public static synchronized void initialize(AppConfig config) throws IllegalArgumentException {
        if (instance == null) {
            if (config == null) {
                throw new IllegalArgumentException(
                        "AppConfig cannot be null during initialization.");
            }
            instance = new TokenManager(config.token());
        } else {
            logger.warn("TokenManager singleton already initialized.");
        }
    }

    public static TokenManager getInstance() throws IllegalStateException {
        if (instance == null) {
            logger.error("TokenManager singleton not initialized when calling getInstance().");
            throw new IllegalStateException(
                    "TokenManager singleton not initialized. Call initialize() first.");
        }

        return instance;
    }

}
