package com.walker.controller;

import com.walker.service.AdminService;
import com.walker.vo.ResultBean;
import com.walker.vo.param.AdminParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author walker
 * @since 2023-02-23
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;



    @ApiOperation(value = "管理员登录")
    @PostMapping("/login")
    public ResultBean login(@RequestBody AdminParam adminParam){
        if (adminParam != null){
            return adminService.login(adminParam.getUsername(),adminParam.getPassword());
        }
        return ResultBean.error("用户名或密码不能为空！");
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }

}
