package com.ccq.controller;

import com.ccq.pojo.Log;
import com.ccq.pojo.User;
import com.ccq.service.LogService;
import com.ccq.service.UserService;
import com.ccq.utils.CommonDate;
import com.ccq.utils.LogUtil;
import com.ccq.utils.NetUtil;
import com.ccq.utils.WordDefined;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author ccq
 * @Description
 * @date 2017/11/28 22:30
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    /**
     * 跳转到聊天页面
     * @return
     */
    @RequestMapping("/chat")
    public ModelAndView getIndex(){
        ModelAndView mv = new ModelAndView("index");
        return mv;
    }

    /**
     * 获取个人头像路径
     * @param userid
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/{userid}/head")
    @ResponseBody
    public User head(@PathVariable("userid") String userid, HttpServletRequest request, HttpServletResponse response){
        User user = userService.getUserById(userid);
        return user;
    }

    /**
     * 跳转到个人信息页面
     * @param userid
     * @return
     */
    @RequestMapping(value = "/{userid}", method = RequestMethod.GET)
    public ModelAndView toInformation(@PathVariable("userid") String userid){
        ModelAndView view = new ModelAndView("information");
        User user = userService.getUserById(userid);
        view.addObject("user",user);
        return view;
    }

    /**
     * 显示个人信息编辑页面
     * @param userid
     * @return
     */
    @RequestMapping(value = "{userid}/config")
    public ModelAndView setting(@PathVariable("userid") String userid){
        ModelAndView view = new ModelAndView("info-setting");
        User user = userService.getUserById(userid);
        view.addObject("user", user);
        return view;
    }

    /**
     * 更新用户个人信息
     * @param userid
     * @param user
     * @param request
     * @return
     */
    @RequestMapping(value = "{userid}/update", method = RequestMethod.POST)
    public String updateUser(@PathVariable("userid") String userid, HttpSession session, User user, RedirectAttributes attributes, HttpServletRequest request){
        int flag = userService.updateUser(user);
        if(flag > 0){
            user = userService.getUserById(userid);
            session.setAttribute("user", user);
            Log log = LogUtil.setLog(userid, CommonDate.getTime24(), WordDefined.LOG_TYPE_UPDATE,WordDefined.LOG_DETAIL_UPDATE_PROFILE, NetUtil.getIpAddress(request));
            logService.insertLog(log);
            attributes.addFlashAttribute("message", "["+userid+"]资料更新成功!");
        }else{
            attributes.addFlashAttribute("error", "["+userid+"]资料更新失败!");
        }
        return "redirect:/{userid}/config";
    }

    /**
     * 修改密码
     * @param userid
     * @param oldpass
     * @param newpass
     * @param attributes
     * @param request
     * @return
     */
    @RequestMapping(value = "{userid}/pass", method = RequestMethod.POST)
    public String updateUserPassword(@PathVariable("userid") String userid,String oldpass, String newpass, RedirectAttributes attributes,HttpServletRequest request){
        User user = userService.getUserById(userid);
        if(oldpass.equals(user.getPassword())){
            user.setPassword(newpass);
            int flag = userService.updateUser(user);
            if(flag > 0){
                Log log = LogUtil.setLog(userid, CommonDate.getTime24(), WordDefined.LOG_TYPE_UPDATE,WordDefined.LOG_DETAIL_UPDATE_PASSWORD, NetUtil.getIpAddress(request));
                logService.insertLog(log);
                attributes.addFlashAttribute("message", "["+userid+"]密码更新成功!");
            }else{
                attributes.addFlashAttribute("error", "["+userid+"]密码更新失败!");
            }
        }else{
            attributes.addFlashAttribute("error", "原密码错误!");
        }
        return "redirect:/{userid}/config";
    }

    @RequestMapping(value = "{userid}/upload", method = RequestMethod.POST,produces = "application/json; charset=utf-8")
    @ResponseBody
    public String updateUserPassword(@PathVariable("userid") String userid,String image,HttpServletRequest request){

        JSONObject responseJson = new JSONObject();
        String filePath = "I:\\IDEA2017-02\\img\\";
        String PicName= UUID.randomUUID().toString()+".png";

        String header ="data:image";
        String[] imageArr=image.split(",");
        if(imageArr[0].contains(header)) {//是img的

            // 去掉头部
            image=imageArr[1];
            // 修改图片
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                byte[] decodedBytes = decoder.decodeBuffer(image); // 将字符串格式的image转为二进制流（biye[])的decodedBytes
                String imgFilePath = filePath + PicName;           //指定图片要存放的位
                File targetFile = new File(filePath);
                if(!targetFile.exists()){
                    targetFile.mkdirs();
                }
                FileOutputStream out = new FileOutputStream(imgFilePath);        //新建一个文件输出器，并为它指定输出位置imgFilePath
                out.write(decodedBytes); //利用文件输出器将二进制格式decodedBytes输出
                out.close();
                // 修改图片
                User user = userService.getUserById(userid);
                user.setProfilehead(PicName);
                int flag = userService.updateUser(user);
                if(flag > 0){
                    Log log = LogUtil.setLog(userid, CommonDate.getTime24(), WordDefined.LOG_TYPE_UPDATE,WordDefined.LOG_DETAIL_UPDATE_PROFILEHEAD, NetUtil.getIpAddress(request));
                    logService.insertLog(log);
                }else{
                    responseJson.put("result","error");
                    responseJson.put("msg","上传失败！");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        responseJson.put("result","ok");
        responseJson.put("msg","上传成功！");
        responseJson.put("fileUrl","/pic/" + PicName);
        return responseJson.toString();
    }

}
