package com.ruijing.registry.admin.data.mapper;

import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * RegistryNodeMapper
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Mapper
public interface RegistryNodeMapper {

    int refresh(@Param("xxlRegistryData") RegistryNodeDO xxlRegistryData);

    int add(@Param("xxlRegistryData") RegistryNodeDO xxlRegistryData);

    List<RegistryNodeDO> findData(@Param("biz") String biz, @Param("env") String env, @Param("key") String key);

    int cleanData(@Param("timeout") int timeout);

    int deleteData(@Param("biz") String biz, @Param("env") String env, @Param("key") String key);

    int deleteDataValue(@Param("biz") String biz, @Param("env") String env, @Param("key") String key, @Param("value") String value);

    int count();
}
