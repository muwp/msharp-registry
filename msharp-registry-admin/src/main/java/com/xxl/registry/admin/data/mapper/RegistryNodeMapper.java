package com.xxl.registry.admin.data.mapper;

import com.xxl.registry.admin.data.model.RegistryNodeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
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
