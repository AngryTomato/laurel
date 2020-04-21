package com.angrytomato.laurel.service;

import com.angrytomato.laurel.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
    String encodePassword(String password);

    boolean isMatch(String rawPasswords, String encryptPassword);

    boolean save(User user);

    User findByUsername(String username);

    boolean existsByUsername(String username);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

}
