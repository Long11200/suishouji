package com.zl.blog.service;

import com.zl.blog.dao.pojo.SysUser;
import com.zl.blog.vo.Result;
import com.zl.blog.vo.UserVo;

/**
 * @author shkstart
 * @create 2022-06-25 13:22
 */
public interface SysUserService {

    UserVo findUserVoById(Long id);

    SysUser findUserById(Long id);

    SysUser findUser(String account, String password);

    Result findUserByToken(String token);

    SysUser findUserByAccount(String account);

    void save(SysUser sysUser);


}
