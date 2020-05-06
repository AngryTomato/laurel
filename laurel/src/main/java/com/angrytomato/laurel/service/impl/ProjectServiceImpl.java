package com.angrytomato.laurel.service.impl;

import com.angrytomato.laurel.Dao.ProjectDao;
import com.angrytomato.laurel.config.LaurelSecurity;
import com.angrytomato.laurel.domain.Storage;
import com.angrytomato.laurel.domain.User;
import com.angrytomato.laurel.service.ProjectService;
import com.angrytomato.laurel.service.UserService;
import com.angrytomato.laurel.util.AESUtils;
import com.angrytomato.laurel.util.RSAUtils;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    LaurelSecurity laurelSecurity;

    @Autowired
    UserService userService;

    @Autowired
    ProjectDao projectDao;

    /**
     * 存储新项目
     * @param storage
     * @return 保存新项目结果
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public boolean save(Storage storage) {
        boolean isSuccess = false;
        try {
            projectDao.save(storage);
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            isSuccess = false;
        }
        return isSuccess;
    }

    /**
     * 加密
     * @param rawStr 原始字符串
     * @param username 用户名
     * @return byte[]类型的加密数据
     */
    @Override
    public byte[] encrypt(String rawStr, String username) {
        byte[] encryptedData = null;
        byte[] laurelPublicKey = Base64.decodeBase64(laurelSecurity.getBase64PublicKey());
        byte[] laurelPrivateKey = Base64.decodeBase64(laurelSecurity.getBase64PrivateKey());

        if (userService.existsByUsername(username)) {//用户存在
            User user = userService.findByUsername(username);
            byte[] enryptedAesKey = user.getEncryptKey();//获得数据库中的用户key
            byte[] encyptedIv = user.getIv();//获取数据库中的用户iv
            try {
                byte[] aesKey = RSAUtils.decryptData(enryptedAesKey, laurelPrivateKey);//解密用户key
                byte[] iv = RSAUtils.decryptData(encyptedIv, laurelPrivateKey);//解密用户iv
                encryptedData = AESUtils.encryptData(rawStr.getBytes(), aesKey, iv, "AES", 256);//使用用户key+iv进行aes256加密
            } catch (Exception e) {
                e.printStackTrace();
                encryptedData = null;
            }
        }
        return encryptedData;
    }

    @Override
    public String decrypt(byte[] encryptedData, String username) {
        String decryptedStr = "";
        byte[] laurelPublicKey = Base64.decodeBase64(laurelSecurity.getBase64PublicKey());
        byte[] laurelPrivateKey = Base64.decodeBase64(laurelSecurity.getBase64PrivateKey());

        if (userService.existsByUsername(username)) {//用户存在
            User user = userService.findByUsername(username);
            byte[] enryptedAesKey = user.getEncryptKey();//获得数据库中的用户key
            byte[] encyptedIv = user.getIv();//获取数据库中的用户iv
            try {
                byte[] aesKey = RSAUtils.decryptData(enryptedAesKey, laurelPrivateKey);//解密用户key
                byte[] iv = RSAUtils.decryptData(encyptedIv, laurelPrivateKey);//解密用户iv
                byte[] decryptedStrBytes = AESUtils.decryptData(encryptedData, aesKey, iv, "AES", 256);//使用用户key+iv进行aes256加密
                decryptedStr = new String(decryptedStrBytes);
            } catch (Exception e) {
                e.printStackTrace();
                decryptedStr = "";
            }
        }
        return decryptedStr;
    }
}
