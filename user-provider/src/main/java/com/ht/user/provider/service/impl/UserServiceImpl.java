package com.ht.user.provider.service.impl;

import com.ht.user.common.interfaces.ConvertBeanUtils;
import com.ht.user.dto.UserDTO;
import com.ht.user.provider.dao.mapper.IUserMapper;
import com.ht.user.provider.dao.po.UserPO;
import com.ht.user.provider.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserServiceImpl
 * @Description: TODO
 * @Author: Torrey
 * @Date: 2024/3/28 16:25
 **/
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private IUserMapper userMapper;


    @Override
    public UserDTO selectById(Long userId) {
        if (userId == null){
            return null;
        }

        return ConvertBeanUtils.convert(userMapper.selectById(userId), UserDTO.class) ;
    }

    @Override
    public boolean insertOne(UserDTO userDTO) {
        if (userDTO == null && userDTO.getUserId() == null){
            return false;
        }
        //插入前检查id重复
        UserDTO userDTO1 = this.selectById(userDTO.getUserId());
        if (userDTO1 != null ){
            throw new RuntimeException("当前id已存在！");

        }
        userMapper.insert(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return true;
    }

    @Override
    public boolean updateOne(UserDTO userDTO) {
        if (userDTO == null && userDTO.getUserId() == null){
            return false;
        }
        userMapper.updateById(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return true;
    }

    @Override
    public boolean deleteByUserId(Long userId) {
        if (userId == null ){
            return false;
        }
        int effectRows = userMapper.deleteById(userId);
        return effectRows == 1 ? true:false;
    }
}
