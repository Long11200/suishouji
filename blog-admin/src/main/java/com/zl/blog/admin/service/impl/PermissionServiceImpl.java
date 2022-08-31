package com.zl.blog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zl.blog.admin.dao.pojo.Permission;
import com.zl.blog.admin.dao.mapper.PermissionMapper;
import com.zl.blog.admin.model.params.PageParam;
import com.zl.blog.admin.service.PermissionService;
import com.zl.blog.admin.vo.PageResult;
import com.zl.blog.admin.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * @author shkstart
 * @create 2022-07-01 11:22
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public Result listPermission(PageParam pageParam) {
        Page<Permission> page = new Page<>(pageParam.getCurrentPage(), pageParam.getPageSize());
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(pageParam.getQueryString())) {
            queryWrapper.like(Permission::getName,pageParam.getQueryString());
        }
        Page<Permission> permissionPage = permissionMapper.selectPage(page, queryWrapper);
        PageResult<Permission> pageResult = new PageResult<>();
        pageResult.setList((permissionPage.getRecords()));
        pageResult.setTotal(page.getTotal());
        return Result.success(pageResult);
    }

    public Result add(Permission permission) {
        this.permissionMapper.insert(permission);
        return Result.success(null);
    }

    public Result update(Permission permission) {
        this.permissionMapper.updateById(permission);
        return Result.success(null);
    }

    public Result delete(Long id) {
        this.permissionMapper.deleteById(id);
        return Result.success(null);
    }
}
