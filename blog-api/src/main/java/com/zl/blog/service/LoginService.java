package com.zl.blog.service;

import com.zl.blog.dao.pojo.SysUser;
import com.zl.blog.vo.Result;
import com.zl.blog.vo.params.LoginParam;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author shkstart
 * @create 2022-06-26 23:28
 */
@Transactional
public interface LoginService {

    Result login(LoginParam loginParam);

    SysUser checkToken(String token);

    Result logout(String token);

    /**
     * sso单点登录
     * @param loginParam
     * @return
     */
    Result register(LoginParam loginParam);
}
