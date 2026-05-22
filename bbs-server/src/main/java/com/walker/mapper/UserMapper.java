package com.walker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walker.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author walker
 * @since 2022/04/17 12:51
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 修改用户角色
     * @param userId 用户id
     * @param roleType 角色类型
     * @return int 返回受影响行数
     */
    int updateUserRole(@Param("userId") Integer userId, @Param("roleType")String roleType);
}
