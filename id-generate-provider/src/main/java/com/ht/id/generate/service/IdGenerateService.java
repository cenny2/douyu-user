package com.ht.id.generate.service;

public interface IdGenerateService {
    /**
     * 获取有序id
     * @param id
     * @return
             */
    Long getSeqId(Integer id);

    /**
     * 获取无序id
     * @param id
     * @return
     */
    Long getUnSeqId(Integer id);
    /**
     * 根据本地步长度来生成唯一id(区间性递增)
     *
     * @param code
     * @return
     */
    String increaseSeqStrId(int code);
}
