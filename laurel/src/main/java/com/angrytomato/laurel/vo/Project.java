package com.angrytomato.laurel.vo;

import lombok.Data;

@Data
public class Project {
    private int id;
    private String projectName;
    private String username;
    private String decryptedPassword;
    private Long storageId;
}
