package com.ccq.service.impl;

import com.ccq.mapper.LogMapper;
import com.ccq.pojo.Log;
import com.ccq.pojo.LogExample;
import com.ccq.service.LogService;
import com.ccq.utils.PageBean;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ccq
 * @Description
 * @date 2017/12/3 16:20
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper logMapper;

    public void insertLog(Log log) {
        logMapper.insert(log);
    }

    public PageBean<Log> selectLogByUserid(String userid, int pageNum, int pageSize) {

        PageBean<Log> pageBean = new PageBean<Log>();

        LogExample logExample = new LogExample();
        logExample.createCriteria().andUseridEqualTo(userid);
        PageHelper.startPage(pageNum,pageSize);
        List<Log> logList = logMapper.selectByExample(logExample);

        PageInfo<Log> logPageInfo = new PageInfo<Log>(logList);

        pageBean.setList(logList);
        pageBean.setTotal(logPageInfo.getTotal());

        return pageBean;
    }
}
