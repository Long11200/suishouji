package com.zl.blog.admin.service;

import com.zl.blog.admin.dao.pojo.Admin;
import com.zl.blog.admin.dao.pojo.Permission;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author shkstart
 * @create 2022-07-02 0:07
 */
@Service
public class authService {

    @Autowired
    private AdminService adminService;

    /**
     * 权限认证
     *
     * @param request
     * @param authentication
     * @return
     */
    public boolean auth(HttpServletRequest request, Authentication authentication) {
        String requestURI = request.getRequestURI();
        //true代表放行 false 代表拦截
        Object principal = authentication.getPrincipal();   //获取当前用户信息
        if (principal == null || "anonymousUser".equals(principal)) {
            //未登录
            return false;
        }
        UserDetails userDetails = (UserDetails) principal;
        String username = userDetails.getUsername();
        Admin admin = adminService.findAdminByUsername(username);
        if (admin == null) {
            return false;
        }
        Long id = admin.getId();
        List<Permission> permissionList = this.adminService.findPermissionByAdminId(id);
        requestURI = StringUtils.split(requestURI,'?')[0];
        for (Permission permission : permissionList) {
            if (requestURI.equals(permission.getPath()))
                return true;
        }
        return false;
    }
}
