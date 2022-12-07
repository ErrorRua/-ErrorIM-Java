package com.errorim.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.errorim.entity.Contact;
import com.errorim.mapper.ContactMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ErrorRua
 * @date 2022/12/02
 * @description:
 */
@SpringBootTest
public class ContactServiceTest {

    @Autowired
    private ContactMapper contactMapper;

    @Test
    void selectFriendTest() {
        LambdaQueryWrapper<Contact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contact::getUserId, "1598609979852476417");

        contactMapper.selectList(queryWrapper).forEach(System.out::println);
    }

}
