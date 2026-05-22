package com.walker.vo.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author chengQing
 * @Date 2026/4/9 16:28
 * @PackageName:com.walker.vo.param
 * @ClassName: UserModOrgNoParam
 * @Description: 修改用户单位参数实体类
 */
@Data
public class UserModOrgNoParam {
    /**
     * 用户id
     */
    @NotBlank(message = "用户标识不能为空")
    private Integer id;
    /**
     * 单位编号
     */
    @NotBlank(message = "单位编号不能为空")
    private String orgNo;
}
