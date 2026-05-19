package com.integration.common.encrypt.util;

import com.integration.common.encrypt.exception.EncryptException;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaUtil {

    private RsaUtil() {
    }

    public static PublicKey getPublicKey(String publicKeyStr) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }

    public static PrivateKey getPrivateKey(String privateKeyStr) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(spec);
    }

    public static String encrypt(String data, String publicKeyStr, String transformation) {
        try {
            PublicKey publicKey = getPublicKey(publicKeyStr);
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new EncryptException("RSA加密失败", e);
        }
    }

    public static String decrypt(String data, String privateKeyStr, String transformation) {
        try {
            PrivateKey privateKey = getPrivateKey(privateKeyStr);
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new EncryptException("RSA解密失败", e);
        }
    }
}
