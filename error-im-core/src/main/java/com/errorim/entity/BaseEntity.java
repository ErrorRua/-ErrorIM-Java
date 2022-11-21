package com.errorim.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 定义基础实体类，固定 id，创建时间、乐观锁、更新时间、逻辑删除
 */
@Data
public class BaseEntity {

    //乐观锁
    @Version
    @TableField(fill= FieldFill.INSERT)
    private Integer version;

    /*逻辑删除*/
    /*@TableLogic*/
    @TableField(fill= FieldFill.INSERT)
    private Integer delFlag;

    //创建时间
    @TableField(fill= FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)		// 反序列化
    @JsonSerialize(using = LocalDateTimeSerializer.class)		// 序列化
    private LocalDateTime createTime;

    //修改时间
    @TableField(fill=FieldFill.INSERT_UPDATE)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)		// 反序列化
    @JsonSerialize(using = LocalDateTimeSerializer.class)		// 序列化
    private LocalDateTime updateTime;
}
