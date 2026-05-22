package com.walker.controller;

import com.walker.pojo.SensitiveWord;
import com.walker.service.SensitiveWordService;
import com.walker.vo.ResultBean;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author chengQing
 * @Date 2026/4/8 17:51
 * @PackageName:com.walker.controller
 * @ClassName: SensitiveWordController
 * @Description: 敏感词控制层
 */
@Api(tags = "SensitiveWordController")
@RestController
public class SensitiveWordController {
    @Autowired
    private SensitiveWordService sensitiveWordService;

    /**
     * 方法描述 查询敏感词列表
     * @author chengQing
     * @date 2026/4/8 18:02
     * @return ResultBean
     */
    @GetMapping("/sensitiveWord/getList")
    public ResultBean getList() {
        List<SensitiveWord> list = sensitiveWordService.getList();
        return ResultBean.success("成功获取！", list);
    }

    /**
     * 方法描述 添加敏感词
     * @author chengQing
     * @date 2026/4/9 10:45
     * @param keyword 敏感词
     * @return ResultBean
     */
    @GetMapping("/sensitiveWord/addSensitiveWord")
    public ResultBean addSensitiveWord(@RequestParam("keyword") String keyword) {
        return sensitiveWordService.addSensitiveWord(keyword);
    }
    /**
     * 方法描述 删除敏感词
     * @author chengQing
     * @date 2026/4/9 10:53
     * @param id 敏感词id
     * @return ResultBean
     */
    @GetMapping("/sensitiveWord/delSensitiveWord")
    public ResultBean delSensitiveWord(@RequestParam("id") Integer id) {
        return sensitiveWordService.delSensitiveWord(id);
    }
}
