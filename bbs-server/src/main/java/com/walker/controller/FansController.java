package com.walker.controller;


import com.walker.pojo.Fans;
import com.walker.service.FansService;
import com.walker.vo.ResultBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author walker
 * @since 2022/05/24 11:10
 */
@RestController
@RequestMapping("/fans")
public class FansController {

    @Autowired
    private FansService fansService;

    @ApiOperation(value = "获取我的关注列表")
    @GetMapping(value = "/getAttentions")
    public List<Map<String,String>> getAttentionList(@RequestParam Integer userId){
        return fansService.getAttentionList(userId);
    }

    @ApiOperation(value = "获取我的粉丝列表")
    @GetMapping(value = "/getFans")
    public List<Map<String,String>> getFansList(@RequestParam Integer userId){
        return fansService.getFansList(userId);
    }

    @ApiOperation(value = "关注")
    @PostMapping(value="/saveFans")
    public ResultBean saveFans(@RequestBody Fans form){
        if (fansService.saveForm(form)){
            return ResultBean.success("关注成功！");
        }
        return ResultBean.error("关注失败！");
    }

    @ApiOperation(value = "取消关注")
    @PostMapping(value = "/cancelFans")
    public ResultBean cancelFans(@RequestBody Fans form){
        if (fansService.cancelFans(form.getUserId(),form.getAttentionId())){
            return ResultBean.success("取消成功！");
        }
        return ResultBean.error("取消失败！");
    }

    @ApiOperation(value = "查询当前用户是否关注了发帖人")
    @GetMapping(value = "/getFansInfo")
    public boolean getFansInfo(@RequestParam Integer userId, @RequestParam Integer attentionId){
        return fansService.getFansInfo(userId,attentionId);
    }

}
