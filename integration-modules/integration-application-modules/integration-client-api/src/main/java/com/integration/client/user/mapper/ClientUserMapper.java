package com.integration.client.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.integration.client.user.entity.ClientUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ClientUserMapper extends BaseMapper<ClientUser> {

    ClientUser selectByOpenid(@Param("openid") String openid);
}
