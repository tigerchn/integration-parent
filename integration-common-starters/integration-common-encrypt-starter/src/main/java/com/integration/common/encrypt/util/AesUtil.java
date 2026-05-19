package com.integration.common.encrypt.util;


import com.integration.common.encrypt.exception.EncryptException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AesUtil {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    private AesUtil() {
    }

    public static String generateKey(int keySize) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(keySize, new SecureRandom());
            return Base64.getEncoder().encodeToString(keyGen.generateKey().getEncoded());
        } catch (Exception e) {
            throw new EncryptException("生成AES密钥失败", e);
        }
    }

    public static String encrypt(String data, String key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new EncryptException("AES加密失败", e);
        }
    }

    public static String decrypt(String data, String key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            byte[] combined = Base64.getDecoder().decode(data);

            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new EncryptException("AES解密失败", e);
        }
    }
}