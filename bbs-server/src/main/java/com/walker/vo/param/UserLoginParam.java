package com.walker.vo.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录实体类
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "UserLogin对象",description = "")
public class UserLoginParam {

    @ApiModelProperty(value = "用户名",required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @ApiModelProperty(value = "密码",required = true)
    @NotBlank(message = "密码不能为空")
    private String password;
    /**
     * 登录渠道，01：用户端，02：服务端
     */
    @ApiModelProperty(value = "登录渠道",required = true)
    @NotBlank(message = "登录渠道不能为空")
    private String channel;
}
