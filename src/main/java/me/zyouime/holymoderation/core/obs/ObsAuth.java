package me.zyouime.holymoderation.core.obs;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class ObsAuth {

    public static String response(String password, String salt, String challenge) {
        String secret = sha256Base64(password + salt);
        return sha256Base64(secret + challenge);
    }

    private static String sha256Base64(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 недоступен", e);
        }
    }
}