package com.walker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walker.pojo.ArticleType;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author walker
 * @since 2022/05/24 11:10
 */
public interface ArticleTypeService extends IService<ArticleType> {

    /**
     * 获取全部的文章类型
     * @return
     */
    List<ArticleType> queryAllArticleType();
}
