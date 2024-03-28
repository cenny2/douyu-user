package com.ht.user.provider.service;

import com.ht.user.rpc.IUserRpcTest;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @ClassName UserRpcTestServiceImpl
 * @Description: TODO
 * @Author: Torrey
 * @Date: 2024/2/28 21:43
 **/
@DubboService(group = "test")
public class UserRpcTestServiceImpl implements IUserRpcTest {
    @Override
    public String test() {
        System.out.println("this is dubbo service ");
        return "success!";
    }
}
