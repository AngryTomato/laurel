package com.angrytomato.laurel.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.angrytomato.laurel.domain.User;
import com.angrytomato.laurel.service.UserService;
import com.angrytomato.laurel.util.RegExpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ProfileController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/user/profile", method = RequestMethod.GET)
    public String profilePage(Model model) {
        Map<String, Object> tips = new HashMap<>();
        tips.put("display", false);
        tips.put("result", true);
        tips.put("message", "");
        model.addAttribute("tips", tips);
        return "profile";
    }

    @ResponseBody
    @RequestMapping(value = "/user/info", method = RequestMethod.GET)
    public JSONObject info(Principal principal) {
        Map<String, Object> info = new HashMap<>();

        String username = principal.getName();//用户名
        if (userService.existsByUsername(username)) {//用户存在
            User user = userService.findByUsername(username);
            info.put("username", user.getUsername());
            info.put("email", user.getEmail());
        } else {//用户不存在
            info.put("username", "");
            info.put("email", "");
        }
        return JSONObject.parseObject(JSON.toJSONString(info));
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public String profile(HttpServletRequest request, Principal principal, Model model) {//
        String username = principal.getName();//获取用户名
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Map<String, Object> tips = new HashMap<>();
        tips.put("display", true);
        tips.put("result", false);
        tips.put("message", "修改个人信息失败");

        if (userService.existsByUsername(username)) {//用户名存在
            User user = userService.findByUsername(username);
            String storageEmail = user.getEmail();//数据库存储的邮箱
            String encryptPassword = user.getPassword();//数据库中的密码
            //密码和邮箱均不为空，且密码正确，且新邮箱和原来的邮箱不相同，新邮箱符合正则
            if (!StringUtils.isEmpty(password) && !StringUtils.isEmpty(email) && userService.isMatch(password, encryptPassword)  && !email.equals(storageEmail) && RegExpUtils.checkEmail(email)) {
                user.setEmail(email);
                user.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                if(userService.save(user)) {//保存数据库成功
                    tips.put("result", true);
                    tips.put("message", "修改个人信息成功");
                }
            }
        }
        model.addAttribute("tips", tips);
        return "profile";
    }
}
