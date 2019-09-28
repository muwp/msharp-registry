package com.ruijing.registry.admin.data.mapper;

import com.ruijing.registry.admin.data.model.RegistryMessageDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
@Mapper
public interface RegistryMessageMapper {

    int add(@Param("xxlRegistryMessage") RegistryMessageDO xxlRegistryMessage);

    List<RegistryMessageDO> findMessage(@Param("excludeIds") List<Integer> excludeIds);

    int cleanMessage(@Param("messageTimeout") int messageTimeout);
}