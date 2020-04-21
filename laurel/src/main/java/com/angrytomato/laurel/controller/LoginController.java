package com.angrytomato.laurel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sun.misc.Request;

@Controller
public class LoginController {
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage() {
        return "signin";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String rootPage() {
        return "signin";
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String successPage() {
        return "success";
    }

    @RequestMapping(value = "/success", method = RequestMethod.POST)
    public String success() {
        return "success";
    }
}
