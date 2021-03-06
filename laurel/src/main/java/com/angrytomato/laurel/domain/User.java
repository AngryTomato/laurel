package com.angrytomato.laurel.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Table(name = "sys_user")
@Entity
@Data
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "encrypt_key", columnDefinition = "blob")
    private byte[] encryptKey;

    @Column(name = "iv", columnDefinition = "blob")
    private byte[] iv;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "update_time")
    private Timestamp updateTime;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "uuid")
    private String uuid;

}
