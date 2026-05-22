package com.walker.controller;


import com.walker.service.ReplyService;
import com.walker.vo.ResultBean;
import com.walker.vo.param.ReplyParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "ReplyController")
@RestController
@RequestMapping("/reply")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    @ApiOperation(value = "保存用户的回复")
    @PutMapping("/userReply")
    public ResultBean userReply(@RequestBody ReplyParam replyParam){

        return replyService.saveUserReply(replyParam);
    }

    @ApiOperation(value = "通过id删除用户评论")
    @PostMapping("/deleteReplyById")
    public ResultBean deleteReplyById(@RequestBody ReplyParam replyParam){
        return replyService.deleteReplyById(replyParam.getReplyId());
    }
}
