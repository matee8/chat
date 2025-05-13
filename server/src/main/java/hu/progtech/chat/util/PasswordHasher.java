package hu.progtech.chat.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    private PasswordHasher() { }

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
