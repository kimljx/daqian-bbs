package com.walker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walker.pojo.Comment;
import com.walker.vo.ResultBean;
import com.walker.vo.param.CommentParam;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author walker
 * @since 2022/05/24 11:10
 */
public interface CommentService extends IService<Comment> {


    /**
     * 保存用户的评论 一级评论
     * @param commentParam
     * @return
     */
    ResultBean saveUserComment(CommentParam commentParam);

    /**
     * 获取评论和回复
     * @return
     */
    List<Comment> queryCommentReply(Integer articleId);

    /**
     * 通过id删除评论
     * @param commentId
     * @return
     */
    ResultBean deleteCommentById(Integer commentId);
}
