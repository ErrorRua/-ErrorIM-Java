package com.errorim.vo;

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
public class MessageVO {
    private String messageId;
    private String fromUserId;
    private String toUserId;
    private String content;
    private Integer type;

    private Date sendTime;
}
