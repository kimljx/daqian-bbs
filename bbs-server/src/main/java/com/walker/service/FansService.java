package com.walker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walker.pojo.Fans;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author walker
 * @since 2022/05/24 11:10
 */
public interface FansService extends IService<Fans> {

    /**
     * 取消关注
     * @param userId
     * @param attentionId
     * @return
     */
    boolean cancelFans(Integer userId, Integer attentionId);

    /**
     * 关注
     * @param form
     * @return
     */
    boolean saveForm(Fans form);

    /**
     * 查询当前用户是否已经关注了当前正在查看的用户
     * @param userId
     * @param attentionId
     * @return
     */
    boolean getFansInfo(Integer userId, Integer attentionId);

    /**
     * 获取我关注的列表
     * @param userId
     * @return
     */
    List<Map<String, String>> getAttentionList(Integer userId);

    /**
     * 获取我的粉丝列表
     * @param userId
     * @return
     */
    List<Map<String, String>> getFansList(Integer userId);
}
