package com.walker.service;

import com.walker.pojo.Admin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.walker.vo.ResultBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author walker
 * @since 2023-02-23
 */
public interface AdminService extends IService<Admin> {


    /**
     * 管理员登录
     * @param username
     * @param password
     * @return
     */
    ResultBean login(String username, String password);
}
