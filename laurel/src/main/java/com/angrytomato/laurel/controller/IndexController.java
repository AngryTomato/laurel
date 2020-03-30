package com.angrytomato.laurel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {
    /**
     * 主页
     * @return
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    /**
     * 主页
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String root() {
        return "index";
    }
}
