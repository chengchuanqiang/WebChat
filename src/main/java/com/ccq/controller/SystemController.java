package com.ccq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author ccq
 * @Description
 * @date 2017/12/17 15:36
 */
@Controller
public class SystemController {

    @RequestMapping(value = "")
    public String index() {
        return "redirect:/user/login";
    }

    @RequestMapping(value = "/about")
    public String about() {
        return "about";
    }

    @RequestMapping(value = "/help")
    public String help() {
        return "help";
    }

    @RequestMapping(value = "/system")
    public String system() {
        return "system-setting";
    }
}
