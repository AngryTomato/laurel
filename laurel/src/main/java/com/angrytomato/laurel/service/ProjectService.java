package com.angrytomato.laurel.service;

import com.angrytomato.laurel.domain.Storage;

import java.util.List;

public interface ProjectService {
    boolean save(Storage storage);
    byte[] encrypt(String rawStr, String username);
    String decrypt(byte[] encryptedData, String username);
    List<Storage> findByUserId(Long userId);
    Storage findByIdAndUserId(Long id, Long userId);
}
