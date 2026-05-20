package com.shuhang.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 当前登录用户更新资料请求
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    private static final long serialVersionUID = 1L;
}
