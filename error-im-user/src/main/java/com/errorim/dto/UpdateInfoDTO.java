package com.errorim.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author ErrorRua
 * @date 2022/11/22
 * @description:
 */
@Data
public class UpdateInfoDTO {

    private String username;

    private String password;

    private String avatar;

    private String email;

    private String sex;

}
