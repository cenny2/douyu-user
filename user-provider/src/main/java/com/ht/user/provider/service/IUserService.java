package com.ht.user.provider.service;

import com.ht.user.dto.UserDTO;
import org.apache.catalina.User;

public interface IUserService {

    UserDTO selectById(Long userId);

    boolean insertOne(UserDTO userDTO);

    boolean updateOne(UserDTO userDTO);

    boolean deleteByUserId(Long userId);
}
