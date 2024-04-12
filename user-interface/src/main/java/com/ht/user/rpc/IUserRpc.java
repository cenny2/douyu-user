package com.ht.user.rpc;

import com.ht.user.dto.UserDTO;

import java.util.List;
import java.util.Map;

/**
 * @ClassName IUserRpcTest
 * @Description: TODO
 * @Author: Torrey
 * @Date: 2024/2/28 21:43
 **/
public interface IUserRpc {

    UserDTO selectById(Long userId);

    Map<Long,UserDTO> batchQueryUserByUserIds(List<Long> userIds);

    boolean insertOne(UserDTO userDTO);

    boolean updateOne(UserDTO userDTO);

    boolean deleteByUserId(Long userId);
}
