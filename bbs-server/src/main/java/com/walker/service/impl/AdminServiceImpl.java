package com.walker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.walker.pojo.Admin;
import com.walker.mapper.AdminMapper;
import com.walker.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walker.vo.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author walker
 * @since 2023-02-23
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {


    @Autowired
    private AdminMapper adminMapper;



    @Override
    public ResultBean login(String username, String password) {

        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, username)
                        .eq(Admin::getPassword, password)
        );
        if (admin != null){
            admin.setPassword(null);
            return ResultBean.success("登录成功！",admin);
        }
        return ResultBean.error("用户名或密码不正确！");
    }
}
