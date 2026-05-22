package com.walker.service;

import com.walker.pojo.SensitiveWord;
import com.walker.vo.ResultBean;

import java.util.List;

/**
 * @Author chengQing
 * @Date 2026/4/8 17:54
 * @PackageName:com.walker.service
 * @ClassName: SensitiveWordService
 * @Description: 敏感词接口层
 */
public interface SensitiveWordService {

    /**
     * 方法描述 查询敏感词列表
     * @author chengQing
     * @date 2026/4/8 18:02
     * @return ResultBean
     */
    List<SensitiveWord> getList();

    /**
     * 方法描述 添加敏感词
     * @author chengQing
     * @date 2026/4/9 10:45
     * @param keyword 敏感词
     * @return ResultBean 返回封装类
     */
    ResultBean addSensitiveWord(String keyword);

    /**
     * 方法描述 删除敏感词
     * @author chengQing
     * @date 2026/4/9 11:02
     * @param id 敏感词id
     * @return ResultBean 返回封装类
     */
    ResultBean delSensitiveWord(Integer id);

    /**
     * 方法描述 项目启动时加载敏感词
     * @author chengQing
     * @date 2026/4/9 15:01
     */
    void loadSensitiveWords();
}
