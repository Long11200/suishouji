package com.zl.blog.admin.service;

import com.zl.blog.admin.dao.pojo.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author shkstart
 * @create 2022-07-01 23:50
 */
@Component
public class SecurityUserService implements UserDetailsService {

    @Autowired
    private AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /**
         * 1.登录的时候,会把username传递到这里
         * 2.通过username查询admin表
         *  如果存在,将密码告诉spring security
         *  如果不存在,返回null,认证失败
         */
        Admin admin = this.adminService.findAdminByUsername(username);
        if (admin == null) {
            return null;
        }
        UserDetails UserDetails = new User(username, admin.getPassword(), new ArrayList<>());
        return UserDetails;
    }
}
