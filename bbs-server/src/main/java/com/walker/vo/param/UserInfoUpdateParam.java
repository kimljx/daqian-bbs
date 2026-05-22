package com.walker.vo.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户信息修改实体类
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "UserInfoUpdate对象",description = "")
public class UserInfoUpdateParam {

    @ApiModelProperty(value = "用户ID",required = true)
    private Integer id;

    @ApiModelProperty(value = "电话",required = true)
    private String phone;
}
