package com.walker.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@TableName("bbs_inventory")
@Accessors(chain = true)
@ApiModel(value = "Inventory", description = "")
public class Inventory {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("area")
    private String area;


    @TableField("category")
    private String category;

    @TableField("type")
    private String type;

    @TableField("name")
    private String name;

    @TableField("content")
    private String content;

    @TableField("snumber")
    private String snumber;

    @TableField("department")
    private String department;

    @TableField("time")
    private String time;

}
