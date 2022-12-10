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
     * @description: 获取好友请求列表
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/12/1
     */
    ResponseResult getFriendRequestList();

    /**
     * @description: 申请添加好友
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/12/1
     */
    ResponseResult addFriendRequest(String friendId);

    /**
     * @description: 同意添加好友
     * @param requestId
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/12/1
     */
    ResponseResult acceptAddFriend(String requestId);

    /**
     * @description: 拒绝添加好友
     * @param requestId
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/12/1
     */
    ResponseResult refuseAddFriend(String requestId);

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
