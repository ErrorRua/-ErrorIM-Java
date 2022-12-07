package com.errorim.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author ErrorRua
 * @date 2022/12/01
 * @description:
 */
@Data
@TableName("us_user_contact")
public class Contact extends BaseEntity{
    @TableId
    private String contactId;

    private String userId;

    private String friendId;
}
