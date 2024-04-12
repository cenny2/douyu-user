package com.ht.user.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.ConvertUtils;
import com.google.common.collect.Maps;
import com.ht.framework.redis.config.RedisConfig;
import com.ht.framework.redis.config.key.UserProviderCacheKeyBuilder;
import com.ht.user.common.interfaces.ConvertBeanUtils;
import com.ht.user.common.interfaces.RandomUtils;
import com.ht.user.dto.UserDTO;
import com.ht.user.provider.dao.mapper.IUserMapper;
import com.ht.user.provider.dao.po.UserPO;
import com.ht.user.provider.service.IUserService;
import jakarta.annotation.Resource;
import org.apache.catalina.User;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.checkerframework.checker.units.qual.A;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName UserServiceImpl
 * @Description: TODO
 * @Author: Torrey
 * @Date: 2024/3/28 16:25
 **/
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private IUserMapper userMapper;

    @Resource
    private RedisTemplate<String,UserDTO> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    @Resource
    private MQProducer producer;

    @Override
    public UserDTO selectById(Long userId) {
        if (userId == null){
            return null;
        }
//        根据规则设置key
        String key = userProviderCacheKeyBuilder.buildUserInfoKey(userId);
        UserDTO userDTO = redisTemplate.opsForValue().get(key);
        if (userDTO != null ){
            return userDTO;
        }
        UserDTO newUserDto = ConvertBeanUtils.convert(userMapper.selectById(userId), UserDTO.class);
        if (newUserDto != null ){
            redisTemplate.opsForValue().set(key,newUserDto,30, TimeUnit.MINUTES);
        }
        return  newUserDto;
    }

    @Override
    public boolean insertOne(UserDTO userDTO) {
        if (userDTO == null && userDTO.getUserId() == null){
            return false;
        }
        //插入前检查id重复
        UserDTO userDTO1 = this.selectById(userDTO.getUserId());
        if (userDTO1 != null ){
            throw new RuntimeException("当前id已存在！");
        }
        userMapper.insert(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return true;
    }

    @Override
    public boolean updateOne(UserDTO userDTO) {
        if (userDTO == null && userDTO.getUserId() == null){
            return false;
        }
        userMapper.updateById(ConvertBeanUtils.convert(userDTO, UserPO.class));
        //删除缓存信息
        redisTemplate.delete(userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId()));
        try {
            //延迟双删策略
            Message message = new Message();
            message.setTopic("userCache");
            //设置消息延迟发送 单位：秒
            message.setDelayTimeLevel(5);
            message.setBody(JSON.toJSONString(userDTO).getBytes());
            producer.send(message);
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        } catch (RemotingException e) {
            throw new RuntimeException(e);
        } catch (MQBrokerException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public boolean deleteByUserId(Long userId) {
        if (userId == null ){
            return false;
        }
        int effectRows = userMapper.deleteById(userId);
        return effectRows == 1 ? true:false;
    }

    @Override
    public Map<Long, UserDTO> batchQueryUserByUserIds(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)){
            return new HashedMap<>();
        }
        List<Long> filterIdList = userIds.stream().filter(userid -> userid > 10000).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterIdList)){
            return Maps.newHashMap();
        }
        //两种方式实现查询
        //先从redis中进行查询，如果存在直接返回
        List<String> cacheKeyList = new ArrayList<>();
//        获取所有查询的userid对应的rediskey
        filterIdList.stream().forEach(userId ->{
            cacheKeyList.add(userProviderCacheKeyBuilder.buildUserInfoKey(userId));
        });
//        查询出缓存中存在的用户信息
        List<UserDTO> cacheContainUserList = redisTemplate.opsForValue().multiGet(cacheKeyList).stream().filter(userDTO -> userDTO != null).collect(Collectors.toList());
        //如果在redis中可以查询出所有数据，则直接返回
        if (CollectionUtils.isNotEmpty(cacheContainUserList) && cacheContainUserList.size() == filterIdList.size()){
            return cacheContainUserList.stream().collect(Collectors.toMap(UserDTO::getUserId,a->a,(k1,k2)->k1));
        }
        if (CollectionUtils.isNotEmpty(cacheContainUserList)){
            //缓存中存在的userid
            List<Long> cahcheInUserIdList  = cacheContainUserList.stream().map(UserDTO::getUserId).collect(Collectors.toList());
            //缓存中不存在的userId
            List<Long> cacheNotInUserId =  filterIdList.stream().filter(userId -> !cahcheInUserIdList.contains(userId)).collect(Collectors.toList());
           //数据查询：将不存在于redis中的userid进行数据库查询
            Map<Long, List<Long>> groupUserId = cacheNotInUserId.stream().collect(Collectors.groupingBy(userId -> userId % 100));
            List<UserDTO> dbQueryResult = new CopyOnWriteArrayList<>();
            groupUserId.values().parallelStream().forEach(userList->{
                List<UserPO> userPOS = userMapper.selectBatchIds(userList);
                List<UserDTO> userDTOList = ConvertBeanUtils.convertList(userPOS, UserDTO.class);
                dbQueryResult.addAll(userDTOList);
            });
            //如果db查询不为空，返回db+redis数据，否则返回redis数据
            if (CollectionUtils.isNotEmpty(dbQueryResult)){
                //将数据存到缓存中
                Map<String, UserDTO> saveCacheMap = dbQueryResult.stream().collect(Collectors.toMap(userDTO -> userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId()), a -> a));
                redisTemplate.opsForValue().multiSet(saveCacheMap);
                redisTemplate.executePipelined(new SessionCallback<Object>() {
                    @Override
                    public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                        for (String key : saveCacheMap.keySet()){
                            operations.expire((K) key, RandomUtils.randomExpireTime(),TimeUnit.SECONDS);
                        }
                        return null;
                    }
                });
                //合并缓存中的数据
                dbQueryResult.addAll(cacheContainUserList);
                return dbQueryResult.stream().collect(Collectors.toMap(UserDTO::getUserId, a -> a, (k1, k2) -> k1));
            }
            return cacheContainUserList.stream().collect(Collectors.toMap(UserDTO::getUserId,a->a,(k1,k2)->k1));
        }else{
            //缓存为空的情况
            Map<Long, List<Long>> groupUserId = filterIdList.stream().collect(Collectors.groupingBy(userId -> userId % 100));
            List<UserDTO> dbQueryList = new ArrayList<>();
            groupUserId.values().parallelStream().forEach(userList->{
                List<UserPO> userPOS = userMapper.selectBatchIds(userList);
                List<UserDTO> userDTOList = ConvertBeanUtils.convertList(userPOS, UserDTO.class);
                dbQueryList.addAll(userDTOList);
            });
            //将数据存入缓存中
            Map<String, UserDTO> saveCacheMap = dbQueryList.stream().collect(Collectors.toMap(userDTO -> userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId()), x -> x));
            redisTemplate.opsForValue().multiSet(saveCacheMap);
            //利用管道设置过期时间
            redisTemplate.executePipelined(new SessionCallback<Object>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    for (String key : saveCacheMap.keySet()){
                        operations.expire((K) key, RandomUtils.randomExpireTime(),TimeUnit.SECONDS);
                    }
                    return null;
                }
            });
            Map<Long, UserDTO> resultMap = dbQueryList.stream().collect(Collectors.toMap(UserDTO::getUserId, a -> a, (k1, k2) -> k1));
            return resultMap;
        }


//        1、直接使用userMapper.batch(),这种方式会产生union all归并结果集，效率比较低
      /*  List<UserPO> userPOS = userMapper.selectBatchIds(filterIdList);
        if (CollectionUtils.isEmpty(filterIdList)){
            return Maps.newHashMap();
        }
        List<UserDTO> userDTOList = ConvertBeanUtils.convertList(userPOS, UserDTO.class);
        Map<Long, UserDTO> userInfoMap = userDTOList.stream().collect(Collectors.toMap(UserDTO::getUserId,a->a,(k1,k2)->k1));
        return userInfoMap;*/
//        2.使用多线程的方式进行查询


//        return null;
    }
}
