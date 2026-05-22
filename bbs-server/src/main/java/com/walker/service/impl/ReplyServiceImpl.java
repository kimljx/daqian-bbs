package com.walker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walker.mapper.ReplyMapper;
import com.walker.pojo.Reply;
import com.walker.service.ReplyService;
import com.walker.vo.ResultBean;
import com.walker.vo.param.ReplyParam;
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
public class ReplyServiceImpl extends ServiceImpl<ReplyMapper, Reply> implements ReplyService {

    @Autowired
    private ReplyMapper replyMapper;
    /**
     * 通过评论获取回复
     * @param commentId
     * @return
     */
    @Override
    public List<Reply> queryReplyByCommentId(Integer commentId) {

        LambdaQueryWrapper<Reply> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Reply::getCommentId,commentId);

        return replyMapper.selectList(lambdaQueryWrapper);
    }


    /**
     * 保存用户的评论
     * @param replyParam
     * @return
     */
    @Override
    public ResultBean saveUserReply(ReplyParam replyParam) {

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String day = format.format(date);

        Reply reply = new Reply();
        reply.setReplyContent(replyParam.getReplyContent());
        reply.setReplyTime(day);
        reply.setReplyUserId(replyParam.getReplyUserId());
        reply.setReplyToUserId(replyParam.getReplyToUserId());
        reply.setCommentId(replyParam.getCommentId());
        this.save(reply);

        return ResultBean.success("回复成功！");
    }

    @Override
    public ResultBean deleteReplyById(Integer replyId) {

        replyMapper.deleteById(replyId);
        return ResultBean.success("删除成功");
    }
}
