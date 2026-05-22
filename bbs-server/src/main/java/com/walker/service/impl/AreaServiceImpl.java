package com.walker.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walker.mapper.AreaMapper;
import com.walker.pojo.Area;
import com.walker.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author walker
 * @since 2022/05/24 11:10
 */
@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {

    @Autowired
    private AreaMapper areaMapper;

    /**
     * 查询 推荐文章、精品社区、最新文章 的图片和图片文字
     * @return
     */
    @Override
    public List<Area> queryArea() {

        return areaMapper.selectList(null);
    }
}
