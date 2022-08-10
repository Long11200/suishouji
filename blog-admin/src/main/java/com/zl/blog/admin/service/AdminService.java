package com.zl.blog.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zl.blog.admin.dao.mapper.AdminMapper;
import com.zl.blog.admin.dao.pojo.Admin;
import com.zl.blog.admin.dao.pojo.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author shkstart
 * @create 2022-07-01 23:51
 */
@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    public Admin findAdminByUsername(String username) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername,username);
        queryWrapper.last(("limit 1"));
        return adminMapper.selectOne(queryWrapper);
    }

    public List<Permission> findPermissionByAdminId(Long id) {
        return this.adminMapper.findPermissionByAdminId(id);
    }
}
