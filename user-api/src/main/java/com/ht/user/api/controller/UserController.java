package com.ht.user.api.controller;

import com.ht.user.dto.UserDTO;
import com.ht.user.rpc.IUserRpc;
import org.apache.catalina.User;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @ClassName UserController
 * @Description: TODO
 * @Author: Torrey
 * @Date: 2024/3/28 17:17
 **/
@Controller
@RequestMapping("user")
public class UserController {

    @DubboReference
    private IUserRpc userRpc;
    @GetMapping("/getUserInfo")
    public UserDTO getUserInfo(Long userId){
        return userRpc.selectById(userId);
    }

    @GetMapping("/insertOne")
    public boolean InsertOne(Long userId,String nickName){
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setNickName(nickName);
        userDTO.setCreateTime(new Date());
        return userRpc.insertOne(userDTO);
    }

    @GetMapping("/deleteByUserId")
    public boolean deleteByUserId(Long userId){
        return userRpc.deleteByUserId(userId);
    }
    @GetMapping("/updateOne")
    public boolean updateOne(Long userId,String trueName){
        UserDTO userDTO = userRpc.selectById(userId);
        userDTO.setTrueName(trueName);
        return userRpc.updateOne(userDTO);
    }
}
