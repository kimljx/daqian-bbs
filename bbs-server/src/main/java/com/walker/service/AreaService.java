package com.walker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walker.pojo.Area;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author walker
 * @since 2022/05/24 11:10
 */
public interface AreaService extends IService<Area> {

    /**
     * 查询 推荐文章、精品社区、最新文章 的图片和图片文字
     * @return
     */
    List<Area> queryArea();
}
