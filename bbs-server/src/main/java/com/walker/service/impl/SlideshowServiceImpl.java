package com.walker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walker.mapper.SlideshowMapper;
import com.walker.pojo.Slideshow;
import com.walker.service.SlideshowService;
import com.walker.vo.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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
public class SlideshowServiceImpl extends ServiceImpl<SlideshowMapper, Slideshow> implements SlideshowService {

    @Autowired
    private SlideshowMapper slideshowMapper;

    /**
     * 获取所有轮播图
     * @return
     */
    @Override
    public List<Slideshow> queryAllSlideshow() {

        return slideshowMapper.selectList(
                new LambdaQueryWrapper<Slideshow>()
                        .orderByDesc(Slideshow::getSuccessive)
                        .last("limit 4")
        );

    }

    @Override
    public ResultBean saveSlideshow(Integer successive, String imageUrl) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());

        Slideshow slideshow = new Slideshow();
        slideshow.setSuccessive(successive)
                        .setImageUrl(imageUrl)
                                .setCreateTime(date);
        slideshowMapper.insert(slideshow);

        return ResultBean.success("保存成功！");
    }

    @Override
    public ResultBean getAllSlideshowByAdmin() {
        List<Slideshow> slideshows = slideshowMapper.selectList(null);
        return ResultBean.success("获取成功！",slideshows);
    }

    @Override
    public ResultBean updateSuccessive(Integer slideshowId, Integer successive) {

        Slideshow slideshow = new Slideshow();
        slideshow.setSlideshowId(slideshowId)
                        .setSuccessive(successive);
        slideshowMapper.updateById(slideshow);

        return ResultBean.success("更新优先级！",slideshow);
    }
}
