package org.stonex.auth.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: stonx
 * @description: 用户实体类
 * @author: zheng_fx
 * @create: 2021-03-03 23:31
 */
@Data
public class User implements Serializable {
    private String username;
    private String password;
    private String nickName;
    private String salt;
    private String token;
}
