package com.integration.admin.rbac.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);
}
