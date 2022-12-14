package com.errorim.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author ErrorRua
 * @date 2022/12/11
 * @description:
 */
@Data
@TableName("chat_message")
public class Message extends BaseEntity {
    @TableId
    private String messageId;

    private String fromUserId;
    private String toUserId;
    private String content;
    private Integer type;

    private Date sendTime;
}
