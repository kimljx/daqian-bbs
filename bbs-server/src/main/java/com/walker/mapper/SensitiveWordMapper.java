package com.walker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walker.pojo.SensitiveWord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author chengQing
 * @Date 2026/4/8 17:56
 * @PackageName:com.walker.mapper
 * @ClassName: SensitiveWordMapper
 * @Description: 敏感词持久层
 */
@Mapper
public interface SensitiveWordMapper extends BaseMapper<SensitiveWord> {

}
