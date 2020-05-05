package com.angrytomato.laurel.util;

import java.util.regex.Pattern;

public class RegExpUtils {
    private final static String REGEXP_UERNAME = "^[a-zA-Z][a-zA-Z0-9_]{4,15}$";//字母开头，允许长度5-16，允许字母数字下划线
    private final static String REGEXP_PASSWORD = "^[a-zA-Z]\\w{5,17}$";//字母开头，允许长度在6~18，允许字母、数字和下划线
    private final static String REGEXP_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";//email
    /**
     * 检查username
     * @param username
     * @return
     */
    public static boolean checkUsername(String username) {
        return Pattern.matches(REGEXP_UERNAME, username);
    }

    /**
     * 检查password
     * @param password
     * @return
     */
    public static boolean checkPassword(String password) {
        return Pattern.matches(REGEXP_PASSWORD, password);
    }

    /**
     * 检查email
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        return Pattern.matches(REGEXP_EMAIL, email);
    }
}
