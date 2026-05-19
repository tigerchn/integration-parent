package com.integration.admin.security;

import com.integration.admin.rbac.entity.AdminUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 封装 {@link AdminUser} 与权限集合，供 Spring Security 认证主体使用。
 */
public class AdminUserDetails implements UserDetails {

    private final AdminUser user;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * @param user         持久化用户实体
     * @param authorities  权限标识集合（通常为 {@code SimpleGrantedAuthority}）
     */
    public AdminUserDetails(AdminUser user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    /** @return 用户主键 */
    public Long getUserId() {
        return user.getId();
    }

    /** {@inheritDoc} */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /** {@inheritDoc} */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /** {@inheritDoc} */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEnabled() {
        return user.getEnabled() != null && user.getEnabled() == 1;
    }
}
