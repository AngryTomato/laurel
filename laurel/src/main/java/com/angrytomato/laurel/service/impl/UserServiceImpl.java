package com.angrytomato.laurel.service.impl;

import com.angrytomato.laurel.Dao.UserDao;
import com.angrytomato.laurel.domain.User;
import com.angrytomato.laurel.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public boolean isMatch(String rawPasswords, String encryptPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPasswords, encryptPassword);
    }

    @Override
    public String encodePassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encryptPassword = encoder.encode(password);
        return encryptPassword;
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public boolean save(User user) {
        boolean isSuccess = false;
        try {
            userDao.save(user);
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            isSuccess = false;
        }
        return isSuccess;
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userDao.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username);
        String getUsername = username;
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        simpleGrantedAuthorities.add(new SimpleGrantedAuthority("role_user"));
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在！");
        }
        String storagePassword  = user.getPassword();

        org.springframework.security.core.userdetails.User securityUser = new org.springframework.security.core.userdetails.User(username, storagePassword, simpleGrantedAuthorities);

        return securityUser;
    }
}
