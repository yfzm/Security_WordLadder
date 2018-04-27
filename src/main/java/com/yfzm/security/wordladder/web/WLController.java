package com.yfzm.security.wordladder.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WLController {
    @RequestMapping(value = "/home")
    public String index()
    {
        return "home";
    }
}
