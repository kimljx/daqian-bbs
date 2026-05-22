package com.walker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walker.pojo.Article;
import com.walker.pojo.ArticleUser;
import com.walker.vo.ResultBean;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author walker
 * @since 2022/12/10 18:03
 */
public interface ArticleUserService extends IService<ArticleUser> {

    /**
     * 保存用户收藏
     * @param userId
     * @param articleId
     * @return
     */
    ResultBean saveUserCollection(Integer userId, Integer articleId);

    /**
     * 用户收藏夹
     * @param userId
     * @return
     */
    List<Article> getMyCollection(Integer userId);

    /**
     * 通过文章的id查询收藏该文章的用户id
     * @param articleIds
     * @return
     */
    List<ArticleUser> getArticleUserByArticleIds(List<Integer> articleIds);
}
