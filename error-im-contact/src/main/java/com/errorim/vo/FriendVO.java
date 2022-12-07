package com.errorim.vo;

import lombok.Data;

/**
 * @author ErrorRua
 * @date 2022/12/02
 * @description:
 */

@Data
public class FriendVO {
    private String userId;
    private String username;
    private String avatar;
    private String sex;
    private String email;

    private Integer isFriend;
}
