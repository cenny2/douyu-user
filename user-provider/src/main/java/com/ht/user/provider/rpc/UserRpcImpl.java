package com.ht.user.provider.rpc;

import com.ht.user.dto.UserDTO;
import com.ht.user.provider.service.IUserService;
import com.ht.user.rpc.IUserRpc;
import com.ht.user.rpc.IUserRpcTest;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @ClassName UserRpcImpl
 * @Description: TODO
 * @Author: Torrey
 * @Date: 2024/3/28 17:14
 **/
@DubboService
public class UserRpcImpl implements IUserRpc {
    @Resource
    private IUserService userService;
    @Override
    public UserDTO selectById(Long userId) {
        return userService.selectById(userId);
    }

    @Override
    public boolean insertOne(UserDTO userDTO) {
        return userService.insertOne(userDTO);
    }

    @Override
    public boolean updateOne(UserDTO userDTO) {
        return userService.updateOne(userDTO);
    }

    @Override
    public boolean deleteByUserId(Long userId) {
        return userService.deleteByUserId(userId);
    }
}
