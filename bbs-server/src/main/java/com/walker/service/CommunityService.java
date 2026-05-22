package com.walker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walker.pojo.Community;
import com.walker.vo.CommunityVO;
import com.walker.vo.ResultBean;
import com.walker.vo.param.CommunityParam;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author walker
 * @since 2022/05/24 11:10
 */
public interface CommunityService extends IService<Community> {

    /**
     * 获取所有的社区名字
     * @return
     */
    List<Community> queryAllCommunity();

    /**
     * 获取所有社区列表
     * @return
     */
    List<CommunityVO> queryAllCommunityList();

    /**
     * 通过社区ID获取社区信息
     * @param communityId
     * @return
     */
    Community queryCommunityById(Integer communityId);

    /**
     * 创建社区
     * @param communityParam
     * @return
     */
    ResultBean createCommunity(CommunityParam communityParam);

    /**
     * 获取所有的社区信息
     * @return
     */
    ResultBean getAllCommunity();


    /**
     * 通过关键词搜索社区
     * @param keywords
     * @return
     */
    ResultBean getCommunityByKeywords(String keywords);


    /**
     * 修改社区的状态
     * @param communityId
     * @return
     */
    ResultBean updateCommunityStatus(Integer communityId);

    /**
     * 删除社区通过社区id
     * @param communityId
     * @return
     */
    ResultBean deleteCommunityByCommunityId(Integer communityId);

    /**
     * 获取论坛数量
     * @return
     */
    ResultBean getCommunityCount();
}
