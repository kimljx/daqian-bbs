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
@ApiModel(value = "MapCityVO对象",description = "")
public class MapCityVO {

    @ApiModelProperty("城市名字")
    private String name;

    @ApiModelProperty("城市的人数")
    private Integer value;

    @ApiModelProperty("占比")
    private String perf;
}
