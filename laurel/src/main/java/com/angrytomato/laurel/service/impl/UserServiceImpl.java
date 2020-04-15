package com.angrytomato.laurel.service.impl;

import com.angrytomato.laurel.Dao.UserDao;
import com.angrytomato.laurel.domain.User;
import com.angrytomato.laurel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    boolean isMatch(String rawPasswords, String encryptPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPasswords, encryptPassword);
    }

    @Override
    public String encodePassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encryptPassword = encoder.encode(password);
        return encryptPassword;
    }

    @Override
    public boolean save(User user) {

    }

    @Override
    public User findUserByUsername(String username) {

    }
}
