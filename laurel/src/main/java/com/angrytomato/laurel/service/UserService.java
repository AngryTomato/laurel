package com.angrytomato.laurel.service;

import com.angrytomato.laurel.domain.User;

public interface UserService {
    String encodePassword(String password);

    boolean isMatch(String rawPasswords, String encryptPassword);

    boolean save(User user);

    User findUserByUsername(String username);
}
