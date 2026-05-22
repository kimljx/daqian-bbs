package com.walker.vo.param;

import lombok.Data;

/**
 * @Author chengQing
 * @Date 2026/3/6 10:29
 * @PackageName:com.walker.vo.param
 * @ClassName: ArticleStatisticParam
 * @Description: 文章统计查询条件
 */
@Data
public class ArticleStatisticParam {
    /**
     * 单位编号
     */
    private String orgNo;
    /**
     * 统计开始日期（yyyy-mm-dd）
     */
    private String startTime;
    /**
     * 统计结束日期（yyyy-mm-dd）
     */
    private String endTime;
    /**
     * 查询单位层级长度（市县所：5-7-9）
     */
    private Integer orgLength;
}
