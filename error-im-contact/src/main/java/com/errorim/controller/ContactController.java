package com.errorim.controller;

import com.errorim.entity.ResponseResult;
import com.errorim.resolver.PostRequestParam;
import com.errorim.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author ErrorRua
 * @date 2022/12/01
 * @description:
 */
@RestController
@RequestMapping("/contact")
public class ContactController {
    @Autowired
    private ContactService contactService;

    // 获取好友列表
    @GetMapping("/list-friend")
    public ResponseResult getUserFriends() {
        return contactService.getUserFriends();
    }

    // 添加好友
    @PostMapping("/add-friend")
    public ResponseResult addFriend(@PostRequestParam String friendId) {
        return contactService.addFriend(friendId);
    }

    // 删除好友
    @DeleteMapping("/delete-friend/{friendId}")
    public ResponseResult deleteFriend(@PathVariable String friendId) {
        return contactService.deleteFriend(friendId);
    }

    // 查找用户
    @GetMapping("/search-user")
    public ResponseResult searchUser(@RequestParam String keyword) {
        return contactService.searchUser(keyword);
    }
}
