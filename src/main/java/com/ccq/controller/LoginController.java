package com.ccq.controller;

import com.ccq.pojo.Log;
import com.ccq.pojo.User;
import com.ccq.service.LogService;
import com.ccq.service.UserService;
import com.ccq.utils.CommonDate;
import com.ccq.utils.LogUtil;
import com.ccq.utils.NetUtil;
import com.ccq.utils.WordDefined;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author ccq
 * @Description 用户登录和注销
 * @date 2017/11/26 14:24
 */
@Controller
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    /**
     * 跳转登录页面
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    /**
     * 用户登录
     * @param userid
     * @param password
     * @param session
     * @param attributes
     * @param request
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(String userid, String password, HttpSession session, RedirectAttributes attributes,HttpServletRequest request) {
        User user = userService.getUserById(userid);
        if(user == null){
            attributes.addFlashAttribute("error",WordDefined.LOGIN_USERID_ERROR);
            return "redirect:/user/login";
        }else {
            if(!user.getPassword().equals(password)){
                attributes.addFlashAttribute("error", WordDefined.LOGIN_PASSWORD_ERROR);
                return "redirect:/user/login";
            }else if(user.getStatus() != 1){
                attributes.addFlashAttribute("error", WordDefined.LOGIN_USERID_DISABLED);
                return "redirect:/user/login";
            } else{
                Log log = LogUtil.setLog(userid,CommonDate.getTime24(),WordDefined.LOG_TYPE_ADD,WordDefined.LOG_DETAIL_USER_LOGIN,NetUtil.getIpAddress(request));
                logService.insertLog(log);
                session.setAttribute("userid", userid);
                session.setAttribute("user", user);
                session.setAttribute("login_status",true);
                user.setLasttime(CommonDate.getTime24());
                userService.updateUser(user);
                attributes.addFlashAttribute("message", WordDefined.LOGIN_SUCCESS);
                return "redirect:/chat";
            }
        }
    }

    /**
     * 用户退出
     * @param session
     * @param attributes
     * @return
     */
    @RequestMapping("/logout")
    public String logout(HttpSession session,RedirectAttributes attributes){
        session.removeAttribute("userid");
        session.removeAttribute("login_status");
        attributes.addFlashAttribute("message",WordDefined.LOGOUT_SUCCESS);
        return "redirect:/user/login";
    }
}
