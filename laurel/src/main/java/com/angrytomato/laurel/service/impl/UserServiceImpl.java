package com.angrytomato.laurel.service.impl;

import com.angrytomato.laurel.Dao.RoleDao;
import com.angrytomato.laurel.Dao.UserDao;
import com.angrytomato.laurel.domain.Role;
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

    @Autowired
    private RoleDao roleDao;

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
            userDao.saveAndFlush(user);
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
        String getUsername = username;
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("------user not found------");
        }
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = getGrantedAuthorities(user);

        String storagePassword  = user.getPassword();

        org.springframework.security.core.userdetails.User securityUser = new org.springframework.security.core.userdetails.User(username, storagePassword, simpleGrantedAuthorities);

        return securityUser;
    }

    private List<SimpleGrantedAuthority> getGrantedAuthorities(User user) {
        List<Role> roles = roleDao.findRoleByUserId(user.getId());
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        for (Role role : roles) {
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        return simpleGrantedAuthorities;
    }


}
