package com.errorim.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author ErrorRua
 * @date 2022/11/20
 * @description:
 */
@Data
@TableName("us_user")
public class User extends BaseEntity{

    @TableId
    private String userId;

    /**
     * 姓名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别
     */
    private String sex;

    /**
     * 邮箱
     */
    private String email;
}
