package hu.progtech.chat.config;

public final class TokenSettings {
    private final String secretKey;
    private final String issuer;

    public TokenSettings(String secretKey, String issuer) {
        this.secretKey = secretKey;
        this.issuer = issuer;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getIssuer() {
        return issuer;
    }

    @Override
    public String toString() {
        return "TokenSettings{secretKey = '" + secretKey + "', issuer = '" + issuer + "'}";
    }
}
