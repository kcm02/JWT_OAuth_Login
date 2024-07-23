package com.project.securelogin.config;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import java.util.Base64;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class JasyptTest {
    public static void main(String[] args) {
        String encryptKey = System.getenv("ENCRYPT_KEY");

        // PooledPBEStringEncryptor 설정
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(encryptKey);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        // 암호화된 문자열 (ENC()로 감싸진 값)
        String encryptedValueWithENC = "ENC(SPLoaRQCoef2nJfCA+ZyHVQfJMcrZmC3AH6OWnPgcY4=)";

        // ENC() 제거
        String base64EncryptedValue = encryptedValueWithENC.replace("ENC(", "").replace(")", "");

        // 복호화
        String decryptedValue = encryptor.decrypt(base64EncryptedValue);

        System.out.println("Decrypted Value: " + decryptedValue);
    }
}

