package com.angrytomato.laurel.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {
    /**
     * 获取当前的UserDetails
     * @return UserDetails对象
     */
    public static UserDetails getCurrentUserDetails() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails;
    }
}
