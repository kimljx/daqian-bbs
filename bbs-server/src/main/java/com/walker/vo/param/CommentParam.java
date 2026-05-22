package com.walker.vo.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "Comment对象",description = "")
public class CommentParam {

    @ApiModelProperty(value = "评论的内容",required = true)
    private String commentContent;

    @ApiModelProperty(value = "评论的文章的ID",required = true)
    private Integer commentArticleId;

    @ApiModelProperty(value = "评论的用户的ID",required = true)
    private Integer commentUserId;


}
