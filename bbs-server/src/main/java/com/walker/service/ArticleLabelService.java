package com.walker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.walker.pojo.ArticleLabel;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author walker
 * @since 2022/05/20 22:07
 */
public interface ArticleLabelService extends IService<ArticleLabel> {


    /**
     * 获取所有的标签
     * @return
     */
    List<ArticleLabel> queryAllArticleLabel();

    /**
     * 根据标签名判断是否已存在
     * @param labelName 标签名称
     * @return true-已存在，false-不存在
     */
    boolean existsByLabelName(String labelName);

    /**
     * 修改标签时：查询除当前labelId以外标签名是否存在
     * @param labelName 标签名称
     * @param labelId 当前标签ID
     * @return true-已存在，false-不存在
     */
    boolean existsByLabelNameExcludeId(String labelName, Integer labelId);

    /**
     * 分页查询文章标签 列表
     * @param pageIndex 页码
     * @param pageSize 每页数据条数
     * @param searchInfo 查询字段（标签名称）
     * @return PageInfo<ArticleLabel>
     */
    PageInfo<ArticleLabel> getAllArticleLabelByPageAndSearch(int pageIndex, int pageSize, String searchInfo);
}
