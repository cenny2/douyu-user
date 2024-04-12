package com.ht.user.provider.service;

import com.ht.user.dto.UserDTO;

import java.util.List;
import java.util.Map;

public interface IUserService {

    UserDTO selectById(Long userId);

    boolean insertOne(UserDTO userDTO);

    boolean updateOne(UserDTO userDTO);

    boolean deleteByUserId(Long userId);

    Map<Long, UserDTO> batchQueryUserByUserIds(List<Long> userIds);
}
