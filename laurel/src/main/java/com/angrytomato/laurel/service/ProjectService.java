package com.angrytomato.laurel.service;

import com.angrytomato.laurel.domain.Storage;

public interface ProjectService {
    boolean save(Storage storage);
    byte[] encrypt(String rawStr, String username);
    String decrypt(byte[] encryptedData, String username);
}
