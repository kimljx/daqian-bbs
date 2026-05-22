package com.walker.controller;


import com.walker.vo.ResultBean;
import com.walker.service.UserService;
import com.walker.vo.param.UserRegisterParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 注册
 */

@Api(tags = "RegisterController")
@RestController
public class RegisterController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "注册")
    @PostMapping("/common/register")
    public ResultBean register(@Validated @RequestBody UserRegisterParam userRegisterParam, HttpServletRequest request){
        if (userRegisterParam != null){
            if (!userRegisterParam.getPassword().equals(userRegisterParam.getRePassword())) {
                return ResultBean.error("两次密码不一致！");
            }
            return userService.register(userRegisterParam.getUsername(),
                    userRegisterParam.getPassword(),
                    userRegisterParam.getPhone(),
                    userRegisterParam.getOrgNo(),
                    userRegisterParam.getNickname(),
                    request);
        }
        return ResultBean.error("参数不能为空！");
    }
}
