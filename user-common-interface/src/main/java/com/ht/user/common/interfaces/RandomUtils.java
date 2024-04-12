package com.ht.user.common.interfaces;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @ClassName RandomUtils
 * @Description: 随机数工具类
 * @Author: Torrey
 * @Date: 2024/4/7 21:53
 **/
public class RandomUtils {

    public static int randomExpireTime(){
        int randomTime = ThreadLocalRandom.current().nextInt(1000);
        return randomTime + 30 * 60;
    }

}
