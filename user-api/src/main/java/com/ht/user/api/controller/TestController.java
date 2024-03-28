package com.ht.user.api.controller;

import com.ht.user.rpc.IUserRpcTest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName TestController
 * @Description: TODO
 * @Author: Torrey
 * @Date: 2024/2/29 21:23
 **/
@Controller
@RequestMapping("user")
public class TestController {
    @DubboReference
    private IUserRpcTest iUserRpcTest;

    @GetMapping("/test")
    public String testDubbo(){
        String test = iUserRpcTest.test();
        return test;
    }
}
