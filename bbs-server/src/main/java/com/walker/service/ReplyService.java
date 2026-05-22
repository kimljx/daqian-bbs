package com.walker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walker.pojo.Reply;
import com.walker.vo.ResultBean;
import com.walker.vo.param.ReplyParam;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author walker
 * @since 2022/05/24 11:10
 */
public interface ReplyService extends IService<Reply> {

    /**
     * 通过评论Id获取回复
     * @param commentId
     * @return
     */
    List<Reply> queryReplyByCommentId(Integer commentId);

    /**
     * 保存用户的品论
     * @param replyParam
     * @return
     */
    ResultBean saveUserReply(ReplyParam replyParam);

    /**
     * 通过id删除用户评论
     * @param replyId
     * @return
     */
    ResultBean deleteReplyById(Integer replyId);
}
