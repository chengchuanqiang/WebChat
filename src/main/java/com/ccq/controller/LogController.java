package com.ccq.controller;

import com.ccq.pojo.Log;
import com.ccq.service.LogService;
import com.ccq.utils.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author ccq
 * @Description
 * @date 2017/12/3 16:51
 */
@Controller
@RequestMapping("")
public class LogController {

    @Autowired
    private LogService logService;

    @RequestMapping("/{userid}/log")
    public ModelAndView selectAll(@PathVariable("userid") String userid, @RequestParam(defaultValue = "1") int page){
        int pageSize = 5;
        ModelAndView view = new ModelAndView("log");
        PageBean<Log> pageBean = logService.selectLogByUserid(userid, page, pageSize);

        view.addObject("list",pageBean.getList());
        view.addObject("count",pageBean.getTotal());

        return view;
    }
}
