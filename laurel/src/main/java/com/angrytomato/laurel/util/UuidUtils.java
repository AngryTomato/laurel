package com.angrytomato.laurel.util;

import java.util.Random;
import java.util.UUID;

public class UuidUtils {
    public static String genUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void main(String[] args) {
        System.out.println(genUuid());
    }
}
