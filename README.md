# laurel

@Version 0.1

@Author AngryTomato

Storage my password

## 1.概要

产生想要开发这个项目的原因就是**1Pasword** 快要过期了，不想续费了。

本项目的功能是存储密码（加密存储），公钥由用户登录后上传（公钥用于加密密码），私钥由用户自身保管（用于解密）。查看密码必须通过用户持有的私钥才能完成。本系统只保存经过公钥加密后的密码字符串。如果用户需要更换秘钥，需要先上传私钥进行解密后再使用新的公钥加密。用户需要妥善保管好自己的秘钥。

鉴于可能在传输过程中被截获，因此采用https的方式。

用户如果上传了私钥、则可以查看解密后的密码；若未上传、则只能查看公钥加密后的密码。解密需要通过自己手中的私钥进行解密。

本项目是基于SpringBoot，使用Spring Security做权限控制。前端页面采用Bootstrap，使用Thymeleaf做为模板引擎。加解密算法采用RSA2048。

## 2.总体设计

### 2.1 权限设计

#### 2.1.1 权限包括

- 录入密码
- 修改密码
- 上传公钥、私钥
- 重置本系统账号密码
- 查询用户
- 创建管理员账户
- 创建角色
- 创建权限
- 对应角色和权限

#### 2.1.2 角色包括：

- 超级管理员：用于创建管理员账号、用于创建角色、创建权限、对应角色和权限；
- 管理员：用于重置普通用户密码；
- 普通用户：普通用户，具有录入、修改密码功能；

#### 2.1.3 权限和角色的对应关系：

- 超级管理员：创建管理员账户、创建角色、创建权限、对应角色和权限；
- 普通管理员：查询用户、重置本系统账号密码；
- 普通用户：录入密码、修改密码、上传公钥、私钥；

### 2.2 数据库表设计

sys_user

|  列  | 类型 | 备注 |
| :--: | :--: | :-: |
| id | bigint(20) | 自增id，主键 |
| username | varchar(255) | 用户名 |
| password | varchar(255) | 用户登录密码 |
| email | varchar(255) | 电子邮件 |
| public_key | blob | 账户的公钥 |
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
| storage_username | varchar(255) |          存储的用户名          |
| encrypt_password |     blob     |       经过公钥加密的密码       |
|       site       | varchar(255) |            网站地址            |
|     user_id      |  bigint(20)  |       对应本系统的用户id       |
|   description    | varchar(255) |            备注描述            |
|   create_time    |   datetime   |            创建时间            |
|   update_time    |   datetime   |            更新时间            |
|    is_deleted    |     bit      | 是否被删除：0-未删除，1-已删除 |
|       uuid       | varchar(255) |              uuid              |

### 2.3 接口设计（对接口要做访问权限控制）

#### 2.3.1 注册用户

访问注册页面：

```
GET /regist
```

注册：

```
POST /signup
```

#### 2.3.2 获取用户

获取所有用户

```
GET /users
```

获取特定用户

```
GET /users/${uid}
```

#### 2.3.3 修改用户信息

```
PUT /users/${uid}
```

#### 2.3.4 删除用户

```
DELETE /users/${uid}
```

#### 2.3.5 新增存储的密码

```
POST /users/${uid}/storages
```

#### 2.3.6 获取存储的密码

获取用户${uid}所有密码

```
GET /users/${uid}/storages
```

获取用户${uid}特定密码

```
GET /users/${uid}/storages/${sid}
```

#### 2.3.7 修改存储的密码

```
PUT /users/${uid}/storages/${sid}
```

#### 2.3.8 增加权限

```
POST /permissions
```

#### 2.3.9 获取权限

```
GET /permissions
```

#### 2.3.10 删除权限

```
DELETE /permissions
```

#### 2.3.11 增加角色

```
POST /roles
```

#### 2.3.12 获取所有角色

```
GET /roles
```

#### 2.3.13 删除角色

```
DELETE /roles
```

#### 2.3.14 修改角色

```
PUT /roles
```

#### 2.3.15 绑定角色和权限

```
POST /roles/${rid}/permissions
```

#### 2.3.16 修改角色和权限绑定

```
PUT /roles/${rid}/permissions
```

#### 2.3.16 新增管理员账户

```
POST /admins
```

#### 2.3.17 删除管理员账户

```
DELETE /admins/${aid}
```

#### 2.3.18 重置普通账户密码

```
PUT /admins/${aid}/users/${uid}
```



