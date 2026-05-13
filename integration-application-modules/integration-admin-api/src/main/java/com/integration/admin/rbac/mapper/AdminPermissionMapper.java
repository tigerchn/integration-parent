package com.integration.admin.rbac.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 管理员权限码查询（用户-角色-权限多表关联）。
 */
@Mapper
public interface AdminPermissionMapper {

    /**
     * @param userId 用户主键
     * @return 去重后的权限编码列表
     */
    @Select("""
            SELECT DISTINCT p.code
            FROM admin_permission p
            INNER JOIN admin_role_permission rp ON p.id = rp.permission_id
            INNER JOIN admin_user_role ur ON ur.role_id = rp.role_id
            WHERE ur.user_id = #{userId}
            """)
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);
}
