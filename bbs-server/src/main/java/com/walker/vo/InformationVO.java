package com.walker.vo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
@ApiModel(value = "InformationVO对象",description = "")
public class InformationVO {

    private Integer userId;
    private String nickName;
    private String portrait;
    private Integer articleId;
    private String articleName;
    private String time;
}
