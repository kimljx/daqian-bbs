package com.walker.vo;

import lombok.Data;

/**
 * @Author chengQing
 * @Date 2026/3/6 10:31
 * @PackageName:com.walker.vo.param
 * @ClassName: ArticleStatisticVO
 * @Description: 文章统计结果返回实体类
 */
@Data
public class ArticleStatisticVO {
    /**
     * 单位编号
     */
    private String orgNo;
    /**
     * 单位名称
     */
    private String orgName;
    /**
     * 文章数
     */
    private Integer articleNumber;
    /**
     * 是否本级（0：否，1：是）
     */
    private Integer isSelf;
}
