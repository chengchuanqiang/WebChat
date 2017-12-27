package com.ccq.service;

import com.ccq.pojo.Log;
import com.ccq.utils.PageBean;

import java.util.List;

/**
 * @author ccq
 * @Description
 * @date 2017/12/3 16:19
 */
public interface LogService {

    public void insertLog(Log log);

    public PageBean<Log> selectLogByUserid(String userid, int page, int pageSize);

}
