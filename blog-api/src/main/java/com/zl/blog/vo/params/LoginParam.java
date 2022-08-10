package com.zl.blog.vo.params;

import lombok.Data;

/**
 * @author shkstart
 * @create 2022-06-26 23:30
 */
@Data
public class LoginParam {

    private String account;

    private String password;

    private String nickname;
}
