package com.angrytomato.laurel.service.impl;

import com.angrytomato.laurel.Dao.RoleUserDao;
import com.angrytomato.laurel.Dao.UserDao;
import com.angrytomato.laurel.domain.RoleUser;
import com.angrytomato.laurel.domain.User;
import com.angrytomato.laurel.service.RegistService;
import com.angrytomato.laurel.util.UuidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.Timestamp;

@Service
public class RegistServiceImpl implements RegistService {
    private static final Long ROLE_USER_ID = 1L;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleUserDao roleUserDao;

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public boolean save(User user) {
        boolean isSuccess = false;
        try {
            userDao.saveAndFlush(user);//保存到sys_user表
            Long userId = user.getId();//获取userId

            RoleUser roleUser = new RoleUser();
            roleUser.setCreateTime(new Timestamp(System.currentTimeMillis()));
            roleUser.setIsDeleted(false);
            roleUser.setRoleId(ROLE_USER_ID);
            roleUser.setUserId(userId);
            roleUser.setUuid(UuidUtils.genUuid());

            roleUserDao.saveAndFlush(roleUser);//保存到sys_role_user表
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            isSuccess = false;
        }
        return isSuccess;
    }
}
