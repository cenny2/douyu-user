package com.ht.id.generate.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ht.id.generate.dao.po.IdBuilderPO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IdGenerateMapper extends BaseMapper<IdBuilderPO> {

    @Select("select * from t_id_generate_config")
    List<IdBuilderPO> selectAll();
}
