package com.angrytomato.laurel.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.net.ssl.KeyStoreBuilderParameters;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtils {
    private final static String ALGORITHM = "RSA";
    /**
     * 获取RSA秘钥对
     * @param keySize
     * @return RSA秘钥对
     * @throws Exception
     */
    public static KeyPair genRSAKeyPair(int keySize) throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    /**
     * 还原公钥
     * @param publicKeyBytes
     * @return 公钥
     * @throws Exception
     */
    public static PublicKey getPublicKey(byte[] publicKeyBytes) throws Exception{
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    /**
     * 还原私钥
     * @param privateKeyBytes
     * @return 私钥
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(byte[] privateKeyBytes) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }

    /**
     * RSA公钥加密
     * @param rawData 原文
     * @param publicKeyBytes 公钥
     * @return 密文
     * @throws Exception
     */
    public static byte[] encryptData(byte[] rawData, byte[] publicKeyBytes) throws Exception{
        PublicKey publicKey = getPublicKey(publicKeyBytes);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(rawData);
    }

    /**
     * RSA私钥解密
     * @param encryptedData 密文
     * @param privateKeyBytes 私钥
     * @return 原文
     * @throws Exception
     */
    public static byte[] decryptData(byte[] encryptedData, byte[] privateKeyBytes) throws Exception{
        PrivateKey privateKey = getPrivateKey(privateKeyBytes);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }
}
