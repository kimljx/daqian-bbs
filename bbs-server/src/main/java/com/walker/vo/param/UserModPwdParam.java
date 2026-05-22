package com.walker.vo.param;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Author chengQing
 * @Date 2026/3/4 10:58
 * @PackageName:com.walker.vo.param
 * @ClassName: UserModPwdParam
 * @Description: 修改用户密码接收参数
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "UserModPwd对象",description = "")
public class UserModPwdParam {
    @ApiModelProperty("用户id")
    private Integer id;

    @ApiModelProperty(value = "原密码",required = true)
    private String password;

    @ApiModelProperty(value = "新密码",required = true)
    private String newPassword;
}
