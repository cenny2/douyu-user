package com.ht.id.generate.service.impl;

import com.ht.id.generate.dao.mapper.IdBuilderMapper;
import com.ht.id.generate.dao.po.IdBuilderPO;
import com.ht.id.generate.service.IdGenerateService;
import com.ht.id.generate.service.bo.LocalSeqIdBO;
import com.ht.id.generate.service.bo.LocalUnSeqIdBO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class IdGenerateServiceImpl implements IdGenerateService, InitializingBean {
    @Resource
    private IdBuilderMapper idBuilderMapper;
    private static Map<Integer, LocalSeqIdBO> localSeqIdMap = new ConcurrentHashMap<>();
    private static Map<Integer, LocalUnSeqIdBO> localUnSeqIdMap = new ConcurrentHashMap<>();
    private static final int SEQ_FLAG = 1;
    private static final int RETRY_TIMES = 3;
    private static final Logger LOGGER = LoggerFactory.getLogger(IdGenerateServiceImpl.class);
    private static ReentrantLock SEQ_LOCK = new ReentrantLock();
    private static ReentrantLock UN_SEQ_LOCK = new ReentrantLock();

    //采用了局部有序性去进行设计，不能保证id段完全用完
    @Override
    public Long getSeqId(Integer code) {
        LocalSeqIdBO localSeqIdBO = localSeqIdMap.get(code);
        if (localSeqIdBO == null) {
            LOGGER.error("[getSeqId] code is error,{}", code);
            return null;
        }
        long returnId = localSeqIdBO.getCurrentValue().getAndIncrement();
        if (returnId - localSeqIdBO.getCurrentStart() > localSeqIdBO.getStep() * 0.75) {
            //进行一个本地id段的更新操作
            this.refreshLocalSeqId(localSeqIdBO.getId());
        }
        return returnId;
    }

    @Override
    public Long getUnSeqId(Integer code) {
        LocalUnSeqIdBO localUnSeqIdBO = localUnSeqIdMap.get(code);
        if (localUnSeqIdBO == null) {
            LOGGER.error("[getUnSeqId] code is error,{}", code);
            return null;
        }
        Long unSeqId = localUnSeqIdBO.getIdQueue().poll();
        if (localUnSeqIdBO.getIdQueue().size() < localUnSeqIdBO.getStep() * 0.25) {
            this.refreshLocalUnSeqId(localUnSeqIdBO.getId());
        }
        return unSeqId;
    }

    @Override
    public String increaseSeqStrId(int code) {
        return null;
    }

    /**
     * 刷新无序id段，加载到本地内存中
     *
     * @param code
     */
    private void refreshLocalUnSeqId(Integer code) {
        boolean lockStatus = false;
        try {
            lockStatus = UN_SEQ_LOCK.tryLock();
            if (lockStatus) {
                for (int i = 0; i < RETRY_TIMES; i++) {
                    IdBuilderPO idBuilderPO = idBuilderMapper.selectById(code);
                    if (idBuilderPO == null) {
                        LOGGER.error("[refreshLocalSeqId] code is error, {}", code);
                        return;
                    }
                    long nextThreshold = idBuilderPO.getNextThreshold() + idBuilderPO.getStep();
                    long currentStart = idBuilderPO.getNextThreshold();
                    int result = idBuilderMapper.updateCurrentThreshold(nextThreshold,currentStart, idBuilderPO.getId(),idBuilderPO.getVersion());
                    if (result < 1) {
                        continue;
                    }
                    LocalUnSeqIdBO localUnSeqIdBO = new LocalUnSeqIdBO();
                    localUnSeqIdBO.setId(idBuilderPO.getId());
                    localUnSeqIdBO.setStep(idBuilderPO.getStep());
                    localUnSeqIdBO.setNextThreshold(nextThreshold);
                    localUnSeqIdBO.setCurrentStart(currentStart);
                    localUnSeqIdBO.setRandomIdInQueue(currentStart, nextThreshold);
                    localUnSeqIdMap.put(idBuilderPO.getId(), localUnSeqIdBO);
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("[refreshLocalUnSeqId] code is {}, error is ", e, code);
        } finally {
            if (lockStatus) {
                UN_SEQ_LOCK.unlock();
            }
        }
    }

    /**
     * 刷新有序id段，加载到本地内存中
     *
     * @param code
     */
    private void refreshLocalSeqId(Integer code) {
        boolean lockStatus = false;
        try {
            lockStatus = SEQ_LOCK.tryLock();
            //防止多线程进入下方程序逻辑中
            if (lockStatus) {
                for (int i = 0; i < RETRY_TIMES; i++) {
                    IdBuilderPO idBuilderPO = idBuilderMapper.selectById(code);
                    if (idBuilderPO == null) {
                        LOGGER.error("[refreshLocalSeqId] code is error, {}", code);
                        return;
                    }
                    long nextThreshold = idBuilderPO.getNextThreshold() + idBuilderPO.getStep();
                    long currentStart = idBuilderPO.getNextThreshold();
                    AtomicLong currentValue = new  AtomicLong(idBuilderPO.getNextThreshold());
                    int result = idBuilderMapper.updateCurrentThreshold(nextThreshold, currentStart, idBuilderPO.getId(),  idBuilderPO.getVersion());
                    if (result < 1) {
                        continue;
                    }
                    LocalSeqIdBO localSeqIdBO = new LocalSeqIdBO();
                    localSeqIdBO.setId(idBuilderPO.getId());
                    localSeqIdBO.setCurrentValue(currentValue);
                    localSeqIdBO.setCurrentStart(currentStart);
                    localSeqIdBO.setNextThreshold(nextThreshold);
                    localSeqIdBO.setStep(idBuilderPO.getStep());
                    localSeqIdMap.put(idBuilderPO.getId(), localSeqIdBO);
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("[refreshLocalSeqId] code is {},error is ", e, code);
        } finally {
            if (lockStatus) {
                SEQ_LOCK.unlock();
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        //在启动之前，将mysql的id配置初始化到本地内存中
        List<IdBuilderPO> idBuilderPOList = idBuilderMapper.selectAll();
        for (IdBuilderPO idBuilderPO : idBuilderPOList) {
            int updateStatus = idBuilderMapper.updateNewVersion(idBuilderPO.getId());
            if (updateStatus > 0) {
                if (idBuilderPO.getIsSeq() == SEQ_FLAG) {
                    LocalSeqIdBO localSeqIdBO = new LocalSeqIdBO();
                    localSeqIdBO.setId(idBuilderPO.getId());
                    localSeqIdBO.setStep(idBuilderPO.getStep());
                    localSeqIdBO.setNextThreshold(idBuilderPO.getNextThreshold() + idBuilderPO.getStep());
                    localSeqIdBO.setCurrentStart(idBuilderPO.getNextThreshold());
                    AtomicLong currentValue = new AtomicLong(idBuilderPO.getNextThreshold());
                    localSeqIdBO.setCurrentValue(currentValue);
                    localSeqIdMap.put(idBuilderPO.getId(),localSeqIdBO);
                } else {
                    LocalUnSeqIdBO localUnSeqIdBO = new LocalUnSeqIdBO();
                    localUnSeqIdBO.setId(idBuilderPO.getId());
                    localUnSeqIdBO.setStep(idBuilderPO.getStep());
                    localUnSeqIdBO.setNextThreshold(idBuilderPO.getNextThreshold() + idBuilderPO.getStep());
                    localUnSeqIdBO.setCurrentStart(idBuilderPO.getNextThreshold());
                    localUnSeqIdBO.setRandomIdInQueue(idBuilderPO.getCurrentStart(), idBuilderPO.getNextThreshold());
                    localUnSeqIdMap.put(idBuilderPO.getId(),localUnSeqIdBO);
                }
            }
        }
    }
}