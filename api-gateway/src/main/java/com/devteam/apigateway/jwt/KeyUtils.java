package com.devteam.apigateway.jwt;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtils {

    private KeyUtils() {}

    public static PublicKey loadPublicKey(final String pemPath) throws Exception {
        final String key = readKeyFromResource(pemPath)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        final byte[] decoded = Base64.getDecoder().decode(key);
        final X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
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
