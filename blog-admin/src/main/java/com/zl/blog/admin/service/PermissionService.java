package com.zl.blog.admin.service;

import com.zl.blog.admin.dao.pojo.Permission;
import com.zl.blog.admin.model.params.PageParam;
import com.zl.blog.admin.vo.Result;

/**
 * @author shkstart
 * @create 2022-07-01 11:20
 */
public interface PermissionService {

    Result listPermission(PageParam pageParam);

    Result add(Permission permission);

    Result update(Permission permission);

    Result delete(Long id);
}
