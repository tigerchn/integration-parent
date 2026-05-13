package com.integration.admin.security;

import com.integration.admin.rbac.entity.AdminUser;
import com.integration.admin.rbac.mapper.AdminUserMapper;
import com.integration.admin.rbac.service.AdminRbacQueryService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 按用户名加载启用状态的管理员，并挂载 RBAC 权限为 {@link org.springframework.security.core.GrantedAuthority}。
 */
@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminUserMapper userMapper;
    private final AdminRbacQueryService rbacQueryService;

    /**
     * @param userMapper       用户表 Mapper
     * @param rbacQueryService 权限查询
     */
    public AdminUserDetailsService(AdminUserMapper userMapper, AdminRbacQueryService rbacQueryService) {
        this.userMapper = userMapper;
        this.rbacQueryService = rbacQueryService;
    }

    /**
     * {@inheritDoc}
     *
     * @throws UsernameNotFoundException 用户不存在或未启用
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        if (user.getEnabled() == null || user.getEnabled() != 1) {
            throw new UsernameNotFoundException(username);
        }
        return new AdminUserDetails(user, rbacQueryService.loadPermissionAuthorities(user.getId()));
    }
}
