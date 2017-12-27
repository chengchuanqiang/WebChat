package com.ccq.utils;

import com.ccq.pojo.Log;

/**
 * @author ccq
 * @Description
 * @date 2017/12/3 16:15
 */
public class LogUtil {

    public static Log setLog(String userId,String time,String type,String detail,String ip){
        Log log = new Log();
        log.setUserid(userId);
        log.setDetail(detail);
        log.setTime(time);
        log.setIp(ip);
        log.setType(type);
        return log;
    }
}
