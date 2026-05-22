package com.walker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walker.pojo.Dict;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author chengQing
 * @Date 2026/3/6 17:32
 * @PackageName:com.walker.mapper
 * @ClassName: DictMapper
 * @Description: 数据字典持久层
 */
@Mapper
public interface DictMapper extends BaseMapper<Dict> {

}
