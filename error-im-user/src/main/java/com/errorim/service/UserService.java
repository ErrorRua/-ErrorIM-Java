package com.errorim.service;

import com.errorim.dto.RegisterDTO;
import com.errorim.dto.UpdateInfoDTO;
import com.errorim.entity.ResponseResult;
import com.errorim.entity.User;

/**
 * @author ErrorRua
 * @date 2022/11/21
 * @description:
 */
public interface UserService {

    /**
     * @description: 注册
     * @param registerDTO:
     * @return com.platform.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/11/21
     */
    ResponseResult register(RegisterDTO registerDTO);

    /**
     * @description: 获取验证码
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/11/22
     */
    ResponseResult getVerifyCode();

    /**
     * @description: 获取用户信息
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/11/22
     */
    ResponseResult getUserInfo();

    /**
     * @description: 通过id获取用户信息
     * @param userId:
     * @return com.errorim.entity.ResponseResult<com.errorim.entity.User>
     * @author ErrorRua
     * @date 2022/11/26
     */
    ResponseResult<User> getUserInfoByUserId(String userId);


    /**
     * @description: 修改用户信息
     * @param updateInfoDTO:
     * @return
     * @author ErrorRua
     * @date 2022/11/22
     */
    ResponseResult updateUserInfo(UpdateInfoDTO updateInfoDTO);

    /**
     * @description: 忘记密码
     * @param :
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/11/22
     */
    ResponseResult forgetPassword();

}
