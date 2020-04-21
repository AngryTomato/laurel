package com.angrytomato.laurel.controller;

import com.angrytomato.laurel.domain.User;
import com.angrytomato.laurel.service.UserService;
import com.angrytomato.laurel.util.UuidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Controller
public class RegistController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/regist", method = RequestMethod.GET)
    public String signUpPage(Model model) {
        Map<String, Object> tips = new HashMap<>();
        tips.put("display", false);
        tips.put("result", true);
        tips.put("message", "");
        model.addAttribute("tips", tips);
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String regist(HttpServletRequest request, Model model) {
        String username = request.getParameter("username");
        String rawpassword = request.getParameter("password");

        User user = new User();
        user.setUsername(username);
        user.setPassword(userService.encodePassword(rawpassword));
        user.setCreateTime(new Timestamp(System.currentTimeMillis()));
        user.setUuid(UuidUtils.genUuid());

        Map<String, Object> tips = new HashMap<>();
        tips.put("display", true);

        if(!userService.existsByUsername(user.getUsername())) {//用户名不存在
            if(userService.save(user)) {//注册写入数据库成功
                tips.put("result", true);
                tips.put("message", "注册成功");
            } else {//注册写入数据库失败
                tips.put("result", false);
                tips.put("message", "注册失败");
            }
        } else {//用户名已经存在
            tips.put("result", false);
            tips.put("message", "用户已经存在");
        }

        //用户名存在
        model.addAttribute("tips", tips);

        return "signup";
    }
}
