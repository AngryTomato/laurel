package com.angrytomato.laurel.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Provider;
import java.security.SecureRandom;

public class AESUtils {
    //获取Provider
    private static Provider provider = new BouncyCastleProvider();
    //填充方式
    private final static String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";

    /**
     * 生成对称密钥
     * @param algorithm
     * @param keySize
     * @return 对称密钥
     * @throws Exception
     */
    public static byte[] genKey(String algorithm, int keySize) throws Exception{
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(keySize, new SecureRandom());
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * 随机生成16 bytes的初始化向量
     * @return 16 bytes的初始化向量
     */
    public static byte[] genRandomIv() {
        byte[] iv = new byte[16];
        SecureRandom ivSecureRandom = new SecureRandom();
        ivSecureRandom.nextBytes(iv);
        return iv;
    }

    /**
     * 加密数据
     * @param rawData 原始数据
     * @param key 秘钥
     * @param iv 向量
     * @param algorithm 算法
     * @param keySize 秘钥长度
     * @return 加密数据
     * @throws Exception
     */
    public static byte[] encryptData(byte[] rawData, byte[] key, byte[] iv, String algorithm, int keySize) throws Exception{
        //加密
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, provider);
        //初始化向量
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        //对称密钥
        SecretKey secretKey = new SecretKeySpec(key, algorithm);
        //加密
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);//CBC
        byte[] encryptedData = cipher.doFinal(rawData);
        return encryptedData;
    }

    /**
     * 解密数据
     * @param encryptedData
     * @param key
     * @param iv
     * @param algorithm
     * @param keySize
     * @return 解密数据
     * @throws Exception
     */
    public static byte[] decryptData(byte[] encryptedData, byte[] key, byte[] iv, String algorithm, int keySize) throws Exception{
        //加密
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, provider);
        //初始化向量
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        //对称密钥
        SecretKey secretKey = new SecretKeySpec(key, algorithm);
        //加密
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);//CBC
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return decryptedData;
    }
}
