package com.errorim.service;

import com.errorim.entity.ResponseResult;

/**
 * @author ErrorRua
 * @date 2022/12/01
 * @description:
 */
public interface ContactService {

    /**
     * @description: 获取用户好友列表
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/12/1
     */
    ResponseResult getUserFriends();

    /**
     * @description: 添加好友
     * @param friendId
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/12/1
     */
    ResponseResult addFriend(String friendId);

    /**
     * @description: 删除好友
     * @param friendId
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/12/1
     */
    ResponseResult deleteFriend(String friendId);

    /**
     * @description: 搜索用户
     * @param email
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/12/1
     */
    ResponseResult searchUser(String email);
}
