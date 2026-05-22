package com.walker.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

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
@TableName("bbs_area")
@ApiModel(value = "Area对象", description = "")
public class Area implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "area_id", type = IdType.AUTO)
    private Integer areaId;

    @ApiModelProperty("图片上的文字描述")
    @TableField("image_title")
    private String imageTitle;

    @ApiModelProperty("图片路径")
    @TableField("image_url")
    private String imageUrl;


}
