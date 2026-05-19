package com.integration.admin.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.integration.admin.rbac.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * {@link AdminUser} 的 MyBatis 数据访问。
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {

    /**
     * @param username 登录名
     * @return 匹配的首条用户，无则 {@code null}
     */
    AdminUser selectByUsername(@Param("username") String username);
}
