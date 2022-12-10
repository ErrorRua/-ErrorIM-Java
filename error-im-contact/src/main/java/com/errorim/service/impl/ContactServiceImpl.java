package com.errorim.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.errorim.entity.AddFriendRequest;
import com.errorim.entity.Contact;
import com.errorim.entity.ResponseResult;
import com.errorim.entity.User;
import com.errorim.mapper.AddFriendRequestMapper;
import com.errorim.mapper.ContactMapper;
import com.errorim.mapper.UserMapper;
import com.errorim.service.ContactService;
import com.errorim.util.BeanCopyUtils;
import com.errorim.util.RedisCache;
import com.errorim.util.SecurityUtils;
import com.errorim.vo.FriendRequestVO;
import com.errorim.vo.FriendVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.errorim.constant.ContactConstant.CACHE_CONTACT_KEY;
import static com.errorim.constant.ContactConstant.CACHE_FRIEND_REQUEST_KEY;
import static com.errorim.enums.ContactEnum.*;
import static com.errorim.enums.UserCodeEnum.USER_NOT_EXIST;

/**
 * @author ErrorRua
 * @date 2022/12/01
 * @description:
 */
@Service
@Slf4j
public class ContactServiceImpl implements ContactService {
    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddFriendRequestMapper addFriendRequestMapper;

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult getUserFriends() {

        String userId = SecurityUtils.getUserId();
        List<FriendVO> friendVOS = null;

        String key = CACHE_CONTACT_KEY + userId;
        friendVOS = redisCache.getCacheObject(key);

        if (Objects.nonNull(friendVOS)) {
            return ResponseResult.okResult(friendVOS);
        }

        LambdaQueryWrapper<Contact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contact::getUserId, userId);

        List<String> friendIdList = contactMapper.selectList(queryWrapper)
                                                .stream()
                                                .map(Contact::getFriendId)
                                                .collect(Collectors.toList());

        friendVOS = BeanCopyUtils.copyBeanList(userMapper.selectBatchIds(friendIdList), FriendVO.class);
        friendVOS.forEach(friendVO -> friendVO.setIsFriend(1));

        // 按用户名排序
        friendVOS.sort(Comparator.comparing(FriendVO::getUsername));

        redisCache.setCacheObject(key, friendVOS, 30, TimeUnit.MINUTES);

        return ResponseResult.okResult(friendVOS);
    }

    @Override
    public ResponseResult getFriendRequestList() {
        String userId = SecurityUtils.getUserId();

        String key = CACHE_FRIEND_REQUEST_KEY + userId;
        List<FriendRequestVO> friendRequestVOS = redisCache.getCacheObject(key);

        if (Objects.nonNull(friendRequestVOS)) {
            return ResponseResult.okResult(friendRequestVOS);
        }

        LambdaQueryWrapper<AddFriendRequest> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddFriendRequest::getToUserId, userId);

        List<AddFriendRequest> addFriendRequests = addFriendRequestMapper.selectList(queryWrapper);
        addFriendRequests.sort(Comparator.comparing(AddFriendRequest::getCreateTime, Comparator.reverseOrder()));


        friendRequestVOS = BeanCopyUtils.copyBeanList(addFriendRequests, FriendRequestVO.class);

        friendRequestVOS.forEach(friendRequestVO -> {
            User user = userMapper.selectById(friendRequestVO.getFromUserId());
            friendRequestVO.setFromUsername(user.getUsername());
            friendRequestVO.setFromUserAvatar(user.getAvatar());
        });


        redisCache.setCacheObject(key, friendRequestVOS, 30, TimeUnit.MINUTES);

        return ResponseResult.okResult(friendRequestVOS);
    }

    @Override
    @Transactional
    public ResponseResult addFriendRequest(String friendId) {
        if (StringUtils.equals(SecurityUtils.getUserId(), friendId)) {
            return ResponseResult.errorResult(CAN_NOT_ADD_SELF.getCode(), CAN_NOT_ADD_SELF.getMessage());
        }

        User user = userMapper.selectById(friendId);

        if (Objects.isNull(user)) {
            return ResponseResult.errorResult(USER_NOT_EXIST.getCode(), USER_NOT_EXIST.getMessage());
        }

        String userId = SecurityUtils.getUserId();

        LambdaQueryWrapper<Contact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contact::getUserId, userId).eq(Contact::getFriendId, friendId);

        Contact contact = contactMapper.selectOne(queryWrapper);

        if (Objects.nonNull(contact)) {
            return ResponseResult.errorResult(IS_FRIEND.getCode(), IS_FRIEND.getMessage());
        }

        addFriendRequestMapper.insert(new AddFriendRequest(){{
            setFromUserId(userId);
            setToUserId(friendId);
        }});

        redisCache.deleteObject(CACHE_FRIEND_REQUEST_KEY + friendId);

        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult acceptAddFriend(String requestId) {
        AddFriendRequest addFriendRequest = addFriendRequestMapper.selectById(requestId);
        if (Objects.isNull(addFriendRequest)) {
            return ResponseResult.errorResult(FRIEND_REQUEST_NOT_EXIST.getCode(), FRIEND_REQUEST_NOT_EXIST.getMessage());
        }

        String userId = SecurityUtils.getUserId();
        String fromUserId = addFriendRequest.getFromUserId();
        String toUserId = addFriendRequest.getToUserId();
        if (!StringUtils.equals(userId, toUserId)) {
            return ResponseResult.errorResult(NOT_YOUR_REQUEST.getCode(), NOT_YOUR_REQUEST.getMessage());
        }

        addFriendRequest.setStatus(1);
        addFriendRequestMapper.updateById(addFriendRequest);

        contactMapper.insert(new Contact() {{
            setUserId(userId);
            setFriendId(fromUserId);
        }});

        contactMapper.insert(new Contact() {{
            setUserId(fromUserId);
            setFriendId(userId);
        }});

        redisCache.deleteObject(CACHE_CONTACT_KEY + userId);
        redisCache.deleteObject(CACHE_CONTACT_KEY + fromUserId);
        redisCache.deleteObject(CACHE_FRIEND_REQUEST_KEY + userId);

        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult refuseAddFriend(String requestId) {
        AddFriendRequest addFriendRequest = addFriendRequestMapper.selectById(requestId);
        if (Objects.isNull(addFriendRequest)) {
            return ResponseResult.errorResult(FRIEND_REQUEST_NOT_EXIST.getCode(), FRIEND_REQUEST_NOT_EXIST.getMessage());
        }

        String userId = SecurityUtils.getUserId();
        String toUserId = addFriendRequest.getToUserId();
        if (!StringUtils.equals(userId, toUserId)) {
            return ResponseResult.errorResult(NOT_YOUR_REQUEST.getCode(), NOT_YOUR_REQUEST.getMessage());
        }

        addFriendRequest.setStatus(-1);
        addFriendRequestMapper.updateById(addFriendRequest);
        redisCache.deleteObject(CACHE_FRIEND_REQUEST_KEY + userId);

        return ResponseResult.okResult();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult deleteFriend(String friendId) {
        String userId = SecurityUtils.getUserId();

        LambdaQueryWrapper<Contact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(Contact::getUserId, userId).eq(Contact::getFriendId, friendId)
                .or()
                .eq(Contact::getUserId, friendId).eq(Contact::getFriendId, userId);

        contactMapper.delete(queryWrapper);

        redisCache.deleteObject(CACHE_CONTACT_KEY + userId);
        redisCache.deleteObject(CACHE_CONTACT_KEY + friendId);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult searchUser(String email) {
//        为了方便前端 isFriend 字段的取值分别是 0 不是好友 1 是好友 2 是自己

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);

        User user = userMapper.selectOne(queryWrapper);

        if (Objects.isNull(user)) {
            return ResponseResult.errorResult(USER_NOT_EXIST.getCode(), USER_NOT_EXIST.getMessage());
        }

        FriendVO friendVO = BeanCopyUtils.copyBean(user, FriendVO.class);
        friendVO.setIsFriend(0);

        LambdaQueryWrapper<Contact> contactQueryWrapper = new LambdaQueryWrapper<>();
        contactQueryWrapper.eq(Contact::getUserId, SecurityUtils.getUserId()).eq(Contact::getFriendId, user.getUserId());

        if (Objects.nonNull(contactMapper.selectOne(contactQueryWrapper))) {
            friendVO.setIsFriend(1);
        }

        // 查询出的用户为自己
        if (Objects.equals(user.getUserId(), SecurityUtils.getUserId())) {
            friendVO.setIsFriend(2);
        }

        return ResponseResult.okResult(friendVO);
    }
}
