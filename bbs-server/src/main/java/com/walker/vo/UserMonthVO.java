package com.walker.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@ApiModel(value = "UserMonthVO对象",description = "")
public class UserMonthVO {

    @ApiModelProperty("时间")
    private String time;

    @ApiModelProperty("注册人数")
    private Integer count;

}
