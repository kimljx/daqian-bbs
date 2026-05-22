package com.walker.vo.param;

import lombok.Data;

/**
 * @Author chengQing
 * @Date 2026/3/10 11:17
 * @PackageName:com.walker.vo.param
 * @ClassName: UserOperationParam
 * @Description: 用户操作参数实体类
 */
@Data
public class UserOperationParam {
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 用户ids,逗号隔开
     */
    private String userIds;
    /**
     * 角色类型 01：普通用户，02：管理员
     */
    private String roleType;

}
