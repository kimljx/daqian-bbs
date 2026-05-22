package com.walker.vo.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author walker
 * @since 2022/05/24 11:10
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "SlideshowParam对象", description = "")
public class SlideshowParam implements Serializable {


    @ApiModelProperty("轮播图的地址")
    private String imageUrl;

    @ApiModelProperty("顺序（数字越大，越靠前）")
    private Integer successive;


}
