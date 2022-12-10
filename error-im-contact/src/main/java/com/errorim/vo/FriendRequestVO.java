package com.errorim.vo;

import lombok.Data;

/**
 * @author ErrorRua
 * @date 2022/12/10
 * @description:
 */
@Data
public class FriendRequestVO {
    private String requestId;
    private String fromUserId;
    private String toUserId;
    private Integer status;
    private String fromUsername;
    private String fromUserAvatar;
}
