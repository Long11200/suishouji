package com.zl.blog.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zl.blog.admin.dao.pojo.Admin;
import com.zl.blog.admin.dao.pojo.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author shkstart
 * @create 2022-07-01 23:52
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    @Select("select * from ms_permission where id in (select permission_id from ms_admin_permission where admin_id=#{id})")
    List<Permission> findPermissionByAdminId(Long id);
}
