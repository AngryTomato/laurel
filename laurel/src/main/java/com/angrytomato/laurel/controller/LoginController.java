package com.angrytomato.laurel.controller;

import com.angrytomato.laurel.service.UserService;
import com.angrytomato.laurel.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage() {
        return "signin";
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String successPage() {
        return "/user/projects";
    }

    @RequestMapping(value = "/success", method = RequestMethod.POST)
    public String success() {
        return "redirect:/user/projects";
    }
}
