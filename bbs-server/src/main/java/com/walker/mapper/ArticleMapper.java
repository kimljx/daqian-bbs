package com.walker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walker.pojo.Article;
import com.walker.vo.PointsRankVO;
import com.walker.vo.param.ArticleStatisticParam;
import com.walker.vo.ArticleStatisticVO;
import com.walker.vo.param.PointsRankParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author walker
 * @since 2022/05/20 14:26
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    /**
     * 方法描述 按单位统计文章数
     * @author chengQing
     * @date 2026/3/6 10:42
     * @param articleStatisticParam 查询条件实体类
     * @return List<ArticleStatisticVO>
     */
    List<ArticleStatisticVO> articleStatisticByOrg(ArticleStatisticParam articleStatisticParam);

    /**
     * 方法描述 获取积分排名
     * @author chengQing
     * @date 2026/3/9 15:02
     * @param pointsRankParam 查询条件实
     * @return List<PointsRankVO> 返回结果集
     */
    List<PointsRankVO> pointsRank(PointsRankParam pointsRankParam);
}
