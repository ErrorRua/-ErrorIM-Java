package com.errorim.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.errorim.entity.Contact;
import com.errorim.entity.ResponseResult;
import com.errorim.entity.User;
import com.errorim.mapper.ContactMapper;
import com.errorim.mapper.UserMapper;
import com.errorim.service.ContactService;
import com.errorim.util.BeanCopyUtils;
import com.errorim.util.RedisCache;
import com.errorim.util.SecurityUtils;
import com.errorim.vo.FriendVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.errorim.constant.ContactConstant.CACHE_CONTACT_KEY;
import static com.errorim.enums.ContactEnum.IS_FRIEND;
import static com.errorim.enums.UserCodeEnum.USER_NOT_EXIST;

/**
 * @author ErrorRua
 * @date 2022/12/01
 * @description:
 */
@Service
public class ContactServiceImpl implements ContactService {
    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private UserMapper userMapper;

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

        redisCache.setCacheObject(key, friendVOS);

        return ResponseResult.okResult(friendVOS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult addFriend(String friendId) {
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

        contactMapper.insert(new Contact() {{
            setUserId(userId);
            setFriendId(friendId);
        }});

        contactMapper.insert(new Contact() {{
            setUserId(friendId);
            setFriendId(userId);
        }});

        String key = CACHE_CONTACT_KEY + userId;
        redisCache.deleteObject(key);

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

        String key = CACHE_CONTACT_KEY + userId;
        redisCache.deleteObject(key);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult searchUser(String email) {
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
