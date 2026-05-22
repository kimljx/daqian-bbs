package com.walker.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author chengQing
 * @Date 2026/4/8 17:49
 * @PackageName:com.walker.pojo
 * @ClassName: SensitiveWord
 * @Description: TODO
 */
@TableName("bbs_sensitive_word")
@ApiModel(value = "SensitiveWord对象", description = "")
@Data
public class SensitiveWord implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("敏感词id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("敏感词")
    @TableField("keyword")
    private String keyword;
}
