package com.sports.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.auth.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    IPage<User> selectUserPage(Page<User> page,
                                @Param("username") String username,
                                @Param("realName") String realName,
                                @Param("role") Integer role,
                                @Param("college") String college,
                                @Param("status") Integer status);
}
