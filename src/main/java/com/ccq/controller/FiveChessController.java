package com.ccq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author ccq
 * @Description
 * @date 2017/12/16 22:59
 */
@Controller
public class FiveChessController {

    /**
     * 跳转到聊天页面
     * @return
     */
    @RequestMapping("/fiveChess")
    public ModelAndView getFiveChess(){
        ModelAndView mv = new ModelAndView("five-chess");
        return mv;
    }
}
