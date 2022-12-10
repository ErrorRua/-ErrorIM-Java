package com.errorim.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author ErrorRua
 * @date 2022/12/10
 * @description:
 */
@Data
@TableName("us_add_request")
public class AddFriendRequest extends BaseEntity{
    @TableId
    private String requestId;
    private String fromUserId;
    private String toUserId;
    private Integer status;
}
