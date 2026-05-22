package com.walker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walker.pojo.Slideshow;
import com.walker.vo.ResultBean;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author walker
 * @since 2022/05/24 11:10
 */
public interface SlideshowService extends IService<Slideshow> {

    /**
     * 获取所有轮播图
     * @return
     */
    List<Slideshow> queryAllSlideshow();


    /**
     * 保存轮播图
     * @param successive
     * @param imageUrl
     * @return
     */
    ResultBean saveSlideshow(Integer successive, String imageUrl);


    /**
     * 获取所有轮播图
     * @return
     */
    ResultBean getAllSlideshowByAdmin();


    /**
     * 修改优先级
     * @param slideshowId
     * @param successive
     * @return
     */
    ResultBean updateSuccessive(Integer slideshowId, Integer successive);
}
