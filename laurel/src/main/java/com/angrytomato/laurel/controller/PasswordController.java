package com.angrytomato.laurel.controller;

import com.angrytomato.laurel.domain.User;
import com.angrytomato.laurel.service.UserService;
import com.angrytomato.laurel.util.RegExpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PasswordController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/user/password", method = RequestMethod.GET)
    public String passwordPage(Model model) {
        Map<String, Object> tips = new HashMap<>();
        tips.put("display", false);
        tips.put("result", true);
        tips.put("message", "");
        model.addAttribute("tips", tips);
        return "password";
    }

    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public String password(HttpServletRequest request, Principal principal, Model model) {
        String username = principal.getName();//获取用户名
        String password = request.getParameter("password");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        Map<String, Object> tips = new HashMap<>();
        tips.put("display", true);
        tips.put("result", false);
        tips.put("message", "修改密码失败");

        if (userService.existsByUsername(username)) {//用户名存在
            User user = userService.findByUsername(username);
            String encryptPassword = user.getPassword();//数据库中的密码
            //三个密码均不为空，且原密码正确，且newPassword 和 confirmPassword 相等，且新密码符合正则
            if (!StringUtils.isEmpty(password) && !StringUtils.isEmpty(newPassword) && !StringUtils.isEmpty(confirmPassword) && userService.isMatch(password, encryptPassword)  && newPassword.equals(confirmPassword) && RegExpUtils.checkPassword(newPassword)) {
                user.setPassword(userService.encodePassword(newPassword));
                user.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                if(userService.save(user)) {//保存数据库成功
                    tips.put("result", true);
                    tips.put("message", "修改密码成功");
                }
            }
        }

        model.addAttribute("tips", tips);
        return "password";
    }
}
