package com.walker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walker.mapper.CommunityMapper;
import com.walker.pojo.Community;
import com.walker.pojo.User;
import com.walker.service.CommunityService;
import com.walker.service.UserService;
import com.walker.vo.CommunityVO;
import com.walker.vo.ResultBean;
import com.walker.vo.param.CommunityParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, Community> implements CommunityService {

    @Autowired
    private CommunityMapper communityMapper;

    @Autowired
    private UserService userService;

    /**
     * 获取所有的社区名字
     * @return
     */
    @Override
    public List<Community> queryAllCommunity() {

        return communityMapper.selectList(
                new LambdaQueryWrapper<Community>()
                        .select(Community::getCommunityName,Community::getCommunityId)
                        .eq(Community::getEnable,0)
        );
    }

    /**
     * 获取所有社区列表
     * @return
     */
    @Override
    public List<CommunityVO> queryAllCommunityList() {

        List<Community> communities = communityMapper.selectList(
                new LambdaQueryWrapper<Community>()
                        .eq(Community::getEnable, 0)
        );
        if (communities != null){
            List<CommunityVO> list = new ArrayList<>();
            communities.forEach(community -> {
                CommunityVO communityVO = new CommunityVO();

                User user = userService.queryUserinfoById(community.getCreateUserId());

                communityVO.setCommunityId(community.getCommunityId())
                        .setCommunityImage(community.getCommunityImage())
                        .setCommunityName(community.getCommunityName())
                        .setCommunityIntroduce(community.getCommunityIntroduce());
                if (user!= null){
                    communityVO.setCreateUserNickname(user.getNickname());
                }else {
                    communityVO.setCreateUserNickname("未知用户");
                }

                list.add(communityVO);
            });
            return list;
        }
        return null;

    }

    /**
     * 通过社区ID获取社区信息
     * @param communityId
     * @return
     */
    @Override
    public Community queryCommunityById(Integer communityId) {

        return this.getById(communityId);
    }

    @Override
    public ResultBean createCommunity(CommunityParam communityParam) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String currentDate = simpleDateFormat.format(new Date());
        Community community = new Community();
        community.setCommunityName(communityParam.getCommunityName())
                .setCommunityIntroduce(communityParam.getCommunityDesc())
                .setCreateUserId(communityParam.getCreateUserId())
                .setCommunityImage(communityParam.getImage())
                .setCreateTime(currentDate);

        communityMapper.insert(community);
        return ResultBean.success("提交成功，待管理员审核！");
    }

    @Override
    public ResultBean getAllCommunity() {

        List<Community> communities = communityMapper.selectList(null);
        return ResultBean.success("获取社区成功！",communities);
    }

    @Override
    public ResultBean getCommunityByKeywords(String keywords) {

        List<Community> communities = communityMapper.selectList(
                new LambdaQueryWrapper<Community>()
                        .like(Community::getCommunityName, keywords)
                        .or()
                        .like(Community::getCommunityIntroduce, keywords)
                        .or()
                        .like(Community::getCreateTime, keywords)
        );
        return ResultBean.success("查询成功！",communities);
    }

    @Override
    public ResultBean updateCommunityStatus(Integer communityId) {

        Community community = communityMapper.selectById(communityId);
        if (community.getEnable() == 1){
            community.setEnable(0);
        }else {
            community.setEnable(1);
        }
        communityMapper.updateById(community);
        return ResultBean.success("修改成功！");
    }

    @Override
    public ResultBean deleteCommunityByCommunityId(Integer communityId) {

        communityMapper.deleteById(communityId);
        return ResultBean.success("删除成功！");
    }

    @Override
    public ResultBean getCommunityCount() {
        Long count = communityMapper.selectCount(null);
        return ResultBean.success("获取成功！",count);
    }
}
