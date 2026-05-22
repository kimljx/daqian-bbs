package com.walker.vo.param;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "UserRegister对象",description = "")
public class UserRegisterParam {

    @ApiModelProperty(value = "账号",required = true)
    @NotBlank(message = "账号不能为空")
    private String username;

    @ApiModelProperty(value = "密码",required = true)
    @NotBlank(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "用户昵称",required = true)
    private String nickname;

    @ApiModelProperty(value = "电话",required = true)
    private String phone;

    @ApiModelProperty(value = "单位编号",required = true)
    @NotBlank(message = "单位不能为空")
    private String orgNo;

    @ApiModelProperty(value = "确认密码",required = true)
    private String rePassword;

}
