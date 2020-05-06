package com.angrytomato.laurel.vo;

import lombok.Data;

@Data
public class ProjectDetail {
    private Long id;
    private String projectName;
    private String username;
    private String decryptedPassword;
    private String site;
    private String remark;
}
