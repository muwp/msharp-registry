package com.ruijing.registry.admin.data.mapper;

import com.ruijing.registry.admin.data.model.ClientNodeDO;
import com.ruijing.registry.admin.data.query.ClientNodeQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * client node mapper
 *
 * @author mwup
 * @version 1.0
 * @created 2018/9/4 17:03
 **/
@Mapper
public interface ClientNodeMapper {

    List<ClientNodeDO> queryForList(ClientNodeQuery query);

    int insertSelective(ClientNodeDO clientNodeDO);

    ClientNodeDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ClientNodeDO clientNodeDO);

    int refresh(ClientNodeDO clientNodeDO);

    List<ClientNodeDO> query(@Param("serviceName") String serviceName,@Param("env") String env);
}