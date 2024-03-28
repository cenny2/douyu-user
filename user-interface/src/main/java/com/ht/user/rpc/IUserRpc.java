package com.ht.user.rpc;

import com.ht.user.dto.UserDTO;

/**
 * @ClassName IUserRpcTest
 * @Description: TODO
 * @Author: Torrey
 * @Date: 2024/2/28 21:43
 **/
public interface IUserRpc {

    UserDTO selectById(Long userId);

    boolean insertOne(UserDTO userDTO);

    boolean updateOne(UserDTO userDTO);

    boolean deleteByUserId(Long userId);
}
