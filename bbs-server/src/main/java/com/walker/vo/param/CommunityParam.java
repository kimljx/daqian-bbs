package com.walker.vo.param;


import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "UserInfoUpdate对象",description = "")
public class CommunityParam {

    private String name;
    private String phone;
    private String email;
    private String communityName;
    private String communityDesc;
    private Integer createUserId;
    private String image;

}
