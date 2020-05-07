# laurel

@Version 1.0

@Author AngryTomato

Storage my password

## 1.概要

产生想要开发这个项目的原因就是**1Pasword** 快要过期了，不想续费了。

本项目的功能是**"存储密码"**（加密存储）。本系统有一个RSA2048秘钥对（系统主秘钥），配置在`application.properties`中，该系统主秘钥用于加解密用户注册时生成一个AES密钥，AES经过加密后会保存在数据库中。该AES密钥用于加解密**"存储密码"**。

由于在生产环境采用明文配置可能会有安全性问题，因此需要对SpringBoot的`application.properties`配置项进行加密（主要是数据库账号密码，以及配置的RSA2048秘钥对——系统主秘钥），此处我们采用的是`jasypt-spring-boot`对配置项进行加密处理。

本项目是基于SpringBoot，使用Spring Security做权限控制。前端页面采用Bootstrap，使用Thymeleaf做为模板引擎。加解密算法采用RSA2048，和AES 256。

## 2.总体设计

### 2.1 数据库表设计

sys_user

|  列  | 类型 | 备注 |
| :--: | :--: | :-: |
| id | bigint(20) | 自增id，主键 |
| username | varchar(255) | 用户名 |
| password | varchar(255) | 用户登录密码 |
| email | varchar(255) | 电子邮件 |
| encrypt_key | blob | 经过加密的对称秘钥（AES 256） |
| iv | blob | 经过加密的16字节的向量 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |
| is_deleted  |     bit      | 是否被删除：0-未删除，1-已删除 |
| uuid | varchar(255) | 用户的uuid |

sys_role

|     列      |     类型     |              备注              |
| :---------: | :----------: | :----------------------------: |
|     id      |  bigint(20)  |          自增id，主键          |
|  role_name  | varchar(255) |            角色名称            |
| create_time |   datetime   |            创建时间            |
| update_time |   datetime   |            更新时间            |
| is_deleted  |     bit      | 是否被删除：0-未删除，1-已删除 |
|    uuid     | varchar(255) |           角色的uuid           |

sys_permission

|       列        |     类型     |              备注              |
| :-------------: | :----------: | :----------------------------: |
|       id        |  bigint(20)  |          自增id，主键          |
| permission_name | varchar(255) |            权限名称            |
|   create_time   |   datetime   |            创建时间            |
|   update_time   |   datetime   |            更新时间            |
|   is_deleted    |     bit      | 是否被删除：0-未删除，1-已删除 |
|      uuid       | varchar(255) |              uuid              |

sys_role_user

|     列      |     类型     |              备注              |
| :---------: | :----------: | :----------------------------: |
|     id      |  bigint(20)  |          自增id，主键          |
|   role_id   |  bigint(20)  |             角色id             |
|   user_id   |  bigint(20)  |             用户id             |
| create_time |   datetime   |            创建时间            |
| update_time |   datetime   |            更新时间            |
| is_deleted  |     bit      | 是否被删除：0-未删除，1-已删除 |
|    uuid     | varchar(255) |              uuid              |

sys_permission_role

|      列       |     类型     |              备注              |
| :-----------: | :----------: | :----------------------------: |
|      id       |  bigint(20)  |          自增id，主键          |
| permission_id |  bigint(20)  |             权限id             |
|    role_id    |  bigint(20)  |             角色id             |
|  create_time  |   datetime   |            创建时间            |
|  update_time  |   datetime   |            更新时间            |
|  is_deleted   |     bit      | 是否被删除：0-未删除，1-已删除 |
|     uuid      | varchar(255) |              uuid              |

sys_storage

|        列        |     类型     |              备注              |
| :--------------: | :----------: | :----------------------------: |
|        id        |  bigint(20)  |          自增id，主键          |
|   project_name   | varchar(255) |      项目名称（用于搜索）      |
| storage_username | varchar(255) |          存储的用户名          |
| encrypt_password |     blob     |         经过加密的密码         |
|       site       | varchar(255) |            网站地址            |
|     user_id      |  bigint(20)  |       对应本系统的用户id       |
|   description    | varchar(255) |            备注描述            |
|   create_time    |   datetime   |            创建时间            |
|   update_time    |   datetime   |            更新时间            |
|    is_deleted    |     bit      | 是否被删除：0-未删除，1-已删除 |
|       uuid       | varchar(255) |              uuid              |

### 2.2 接口设计

#### 2.2.1 注册登录

访问注册页面：

```
GET /regist
```

注册：

```
POST /signup
```

访问登录页面：

```
GET /login
```

登录（Security配置）：

```
loginProcessingUrl("/signin")
```

登录成功（Security配置）：

```
successForwardUrl("/success")
```

#### 2.2.2 修改用户信息

1.用户信息修改

访问修改用户信息页面：

```
GET /user/profile
```

获取当前用户信息：

```
GET /user/info
```

修改用户信息:

```
POST /profile
```

2.用户密码修改

访问修改密码页面：

```
GET /user/password
```

修改密码：

```
POST /password
```

#### 2.2.3 操作存储的密码

1.新增存储密码

访问新增密码页面：

```
GET /user/storage
```

新增密码：

```
POST /storage
```

2.存储密码查询

访问密码查询页面（即“项目”页面）：

```
GET /user/projects
```

查询存储密码：

```
GET /projects/search
```

获取某条密码详情：

```
POST /projects/details
```

3.修改存储的密码：

```
POST /projects/details/update
```

4.删除存储的密码：

```
POST /projects/details/delete
```

