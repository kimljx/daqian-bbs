package com.walker.controller;


import com.walker.service.CommunityUserService;
import com.walker.vo.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author walker
 * @since 2022/05/24 11:10
 */
@RestController
public class CommunityUserController {

    @Autowired
    private CommunityUserService communityUserService;

    /**
     * 判断是否已经加入社区
     * @param communityId 社区ID
     * @param userId 用户ID
     * @return 是否已经加入
     */
    @GetMapping("/join")
    public boolean join(@RequestParam Integer communityId, @RequestParam Integer userId){
        return communityUserService.join(communityId,userId);
    }


    /**
     * 退出社区
     * @param communityId 社区ID
     * @param userId 用户ID
     * @return json
     */
    @DeleteMapping("/delete")
    public ResultBean delete(@RequestParam Integer communityId, @RequestParam Integer userId){
        if (communityUserService.delete(communityId,userId)){
            return ResultBean.success("退出成功！");
        }
        return ResultBean.error("退出失败！");
    }


    @PostMapping("/saveCommunityUser")
    public ResultBean saveCommunityUser(@RequestParam Integer communityId, @RequestParam Integer userId){

        return communityUserService.saveCommunityUser(communityId,userId);
    }

}
