package com.ht.user.provider.rpc;

import com.ht.user.dto.UserDTO;
import com.ht.user.provider.service.IUserService;
import com.ht.user.rpc.IUserRpc;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;
import java.util.Map;

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
    public Map<Long, UserDTO> batchQueryUserByUserIds(List<Long> userIds) {
        return userService.batchQueryUserByUserIds(userIds);
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
