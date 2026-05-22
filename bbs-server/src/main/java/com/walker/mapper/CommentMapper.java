package com.walker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walker.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author walker
 * @since 2022/05/24 11:10
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

}
