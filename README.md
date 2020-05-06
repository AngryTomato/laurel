# laurel

@Version 0.1

@Author AngryTomato

Storage my password

## 1.概要

产生想要开发这个项目的原因就是**1Pasword** 快要过期了，不想续费了。

本项目的功能是存储密码（加密存储）。用户注册时，系统会返回用户一个由系统生成的AES密钥（暂且称为对称秘钥A），用户必须保管好该密钥。该密钥用于**存储密码**的加解密，且该密钥会加密存储在数据库中。

该系统中会存在一个系统秘钥对（暂且称为非对称秘钥对B）。该密钥对用于对对称秘钥A进行加解密。

生产环境采用明文配置可能会有安全性问题，因此需要对SpringBoot的`application.properties`配置项进行加密，此处我们采用的是jasypt。

鉴于可能在传输过程中被截获，因此采用https的方式。

本项目是基于SpringBoot，使用Spring Security做权限控制。前端页面采用Bootstrap，使用Thymeleaf做为模板引擎。加解密算法采用RSA2048，和AES 256。

打算第一期先做普通用户相关功能。第二期做管理员和超级管理员相关功能（*标的都是第二期需要做的）。

## 2.总体设计

### 2.1 权限设计

#### 2.1.1 权限包括

- 录入密码
- 修改密码
- 更新对称秘钥
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
- 普通用户：录入密码、修改密码、修改对称秘钥（AES 256）；

### 2.2 数据库表设计

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

### 2.3 接口设计（对接口要做访问权限控制）

#### 2.3.1 注册登录用户

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

登录（这个由SpringSecurity配置）：

```
.loginProcessingUrl("/signin")
```

#### 2.3.2 获取用户(*)

获取所有普通用户

```
GET /admins/users
```

获取特定普通用户

```
GET /admins/users/${uid}
```

获取管理员用户

```
GET /superadmins/admins
```

获取特定管理员账户

```
GET /superadmins/admins/${aid}
```

#### 2.3.3 修改用户信息

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

访问修改密码页面：

```
GET /user/password
```

修改密码：

```
POST /password
```

#### 2.3.4 删除用户(*)

```
DELETE /users/${uid}
```

#### 2.3.5 新增存储的密码

访问新增密码页面

```
GET /user/storage
```

新增密码

```
POST /storage
```

#### 2.3.6 获取存储的密码

访问密码列表页面（即“项目”页面）

```
GET /user/projects
```

获取用户${uid}密码列表

```
POST /projects/details
```

#### 2.3.7 修改存储的密码

```
POST /projects/details/update
```

#### 2.3.8 增加权限(*)

```
POST /superadmins/permissions
```

#### 2.3.9 获取权限(*)

```
GET /superadmins/permissions
```

#### 2.3.10 删除权限(*)

```
DELETE /superadmins/permissions/${pid}
```

#### 2.3.11 增加角色(*)

```
POST /superadmins/roles
```

#### 2.3.12 获取所有角色(*)

```
GET /superadmins/roles
```

#### 2.3.13 删除角色(*)

```
DELETE /superadmins/roles
```

#### 2.3.14 修改角色(*)

```
PUT /superadmins/roles
```

#### 2.3.15 绑定角色和权限(*)

```
POST /superadmins/roles/${rid}/permissions
```

#### 2.3.16 修改角色和权限绑定(*)

```
PUT /superadmins/roles/${rid}/permissions
```

#### 2.3.16 新增管理员账户(*)

```
POST /superadmins/admins
```

#### 2.3.17 删除管理员账户(*)

```
DELETE /superadmins/admins/${aid}
```

#### 2.3.18 重置普通账户密码(*)

```
PUT /admins/${aid}/users/${uid}
```



