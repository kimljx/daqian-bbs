package com.walker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walker.mapper.CommunityUserMapper;
import com.walker.pojo.CommunityUser;
import com.walker.service.CommunityUserService;
import com.walker.vo.ResultBean;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author walker
 * @since 2022/05/24 11:10
 */
@Service
public class CommunityUserServiceImpl extends ServiceImpl<CommunityUserMapper, CommunityUser> implements CommunityUserService {

    @Autowired
    private CommunityUserMapper communityUserMapper;

    @Override
    public ResultBean saveCommunityUser(Integer communityId, Integer userId) {

        LambdaQueryWrapper<CommunityUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CommunityUser::getCommunityId, communityId).eq(CommunityUser::getUserId, userId);
        CommunityUser communityUser = communityUserMapper.selectOne(lambdaQueryWrapper);

        if (communityUser == null){
            CommunityUser communityUser1 = new CommunityUser();
            communityUser1.setCommunityId(communityId);
            communityUser1.setUserId(userId);
            communityUserMapper.insert(communityUser1);
            return ResultBean.success("成功加入社区！");
        }
        return ResultBean.error("该用户已在社区内，请不要重复加入！");
    }

    @Override
    public boolean join(Integer communityId, Integer userId) {
        CommunityUser communityUser = getBaseMapper().selectOne(new LambdaQueryWrapper<CommunityUser>()
                .eq(CommunityUser::getCommunityId, communityId)
                .eq(CommunityUser::getUserId, userId)
        );
        return ObjectUtils.isNotEmpty(communityUser);
    }

    @Override
    public boolean delete(Integer communityId, Integer userId) {
        int delete = getBaseMapper().delete(new LambdaQueryWrapper<CommunityUser>()
                .eq(CommunityUser::getCommunityId, communityId)
                .eq(CommunityUser::getUserId, userId)
        );
        return delete == 1;
    }
}
