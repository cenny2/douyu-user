package com.ht.id.generate.rpc;

import com.ht.id.generate.interfaces.IdGenerateRpc;
import com.ht.id.generate.service.IdGenerateService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class IdGenerateRpcImpl implements IdGenerateRpc {

    @Resource
    private IdGenerateService generateService;

    @Override
    public Long getSeqId(Integer id) {
        return generateService.getSeqId(id);
    }

    @Override
    public Long getUnSeqId(Integer id) {
        return generateService.getUnSeqId(id);
    }

    @Override
    public String increaseSeqStrId(int code) {
        return generateService.increaseSeqStrId(code);
    }
}
