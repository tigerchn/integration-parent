package com.integration.admin.rbac.service;

import com.integration.admin.rbac.mapper.AdminPermissionMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 管理端 RBAC 查询：将数据库中的权限码转为 Spring Security 权限对象。
 */
@Service
public class AdminRbacQueryService {

    private final AdminPermissionMapper permissionMapper;

    /**
     * @param permissionMapper 权限 Mapper
     */
    public AdminRbacQueryService(AdminPermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    /**
     * @param userId 用户主键
     * @return 可作为 {@link org.springframework.security.core.GrantedAuthority} 使用的权限集合
     */
    public Collection<GrantedAuthority> loadPermissionAuthorities(Long userId) {
        List<String> codes = permissionMapper.selectPermissionCodesByUserId(userId);
        return codes.stream().map(c -> (GrantedAuthority) new SimpleGrantedAuthority(c)).toList();
    }
}
