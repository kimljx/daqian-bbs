package com.walker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walker.pojo.CommunityUser;
import com.walker.vo.ResultBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author walker
 * @since 2022/05/24 11:10
 */
public interface CommunityUserService extends IService<CommunityUser> {

    /**
     * 保存用户加入的社区
     * @param communityId
     * @param userId
     * @return
     */
    ResultBean saveCommunityUser(Integer communityId, Integer userId);

    /**
     * 用户是否加入了改社区
     * @param communityId 社区ID
     * @param userId 用户ID
     * @return 是否已经加入
     */
    boolean join(Integer communityId, Integer userId);

    /**
     * 退出社区
     * @param communityId 社区ID
     * @param userId 用户ID
     * @return 是否退出成功
     */
    boolean delete(Integer communityId, Integer userId);
}
