package com.ht.user.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ht.user.provider.dao.po.UserPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName UserMapper
 * @Description: TODO
 * @Author: Torrey
 * @Date: 2024/3/28 16:23
 **/
@Mapper
public interface IUserMapper extends BaseMapper<UserPO> {

}
