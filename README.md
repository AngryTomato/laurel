# laurel
Storage my password

## 1.概要

产生想要开发这个项目的原因就是**1Pasword** 快要过期了，不想续费了。

本项目的功能是存储密码（加密存储），公钥由用户登录后上传（公钥用于加密密码），私钥由用户自身保管（用于解密）。查看密码必须通过用户持有的私钥才能完成。

本项目是基于SpringBoot，使用Spring Security做权限控制。前端页面采用Bootstrap，使用Thymeleaf做为模板引擎。加解密算法采用RSA2048。

## 2.总体设计

