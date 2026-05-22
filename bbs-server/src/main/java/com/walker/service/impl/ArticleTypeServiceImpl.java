package com.walker.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walker.mapper.ArticleTypeMapper;
import com.walker.pojo.ArticleType;
import com.walker.service.ArticleTypeService;
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
public class ArticleTypeServiceImpl extends ServiceImpl<ArticleTypeMapper, ArticleType> implements ArticleTypeService {

    @Autowired
    private ArticleTypeMapper articleTypeMapper;

    /**
     * 获取全部的文章类型
     * @return
     */
    @Override
    public List<ArticleType> queryAllArticleType() {

        List<ArticleType> articleTypes = articleTypeMapper.selectList(null);
        return articleTypes;
    }
}
