package com.devteam.identityservice.security;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtils {

    private KeyUtils() {}

    /**
     * Load private key from file system (for Render Secret Files) or classpath resource
     */
    public static PrivateKey loadPrivateKey(final String pemPath) throws Exception {
        final String key = readKey(pemPath)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        final byte[] decoded = Base64.getDecoder().decode(key);
        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    /**
     * Load public key from file system (for Render Secret Files) or classpath resource
     */
    public static PublicKey loadPublicKey(final String pemPath) throws Exception {
        final String key = readKey(pemPath)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        final byte[] decoded = Base64.getDecoder().decode(key);
        final X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    /**
     * Try to read key from file system first (Render Secret Files at /etc/secrets/)
     * If not found, fallback to classpath resource
     */
    private static String readKey(final String pemPath) throws Exception {
        // Try reading from Render Secret Files location first
        Path secretFilePath = Paths.get("/etc/secrets/" + getFileName(pemPath));
        if (Files.exists(secretFilePath)) {
            return Files.readString(secretFilePath);
        }
        
        // Fallback to classpath resource for local development
        return readKeyFromResource(pemPath);
    }

    private static String getFileName(final String path) {
        return Paths.get(path).getFileName().toString();
    }

    private static String readKeyFromResource(final String pemPath) throws Exception {
        try (final InputStream inputStream = KeyUtils.class.getClassLoader().getResourceAsStream(pemPath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Key file does not exist (Path: " + pemPath + ")");
            }
            return new String(inputStream.readAllBytes());
        }
    }

}
