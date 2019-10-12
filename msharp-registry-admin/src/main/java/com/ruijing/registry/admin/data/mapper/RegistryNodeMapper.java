package com.ruijing.registry.admin.data.mapper;

import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.data.query.RegistryNodeQuery;
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

    int refresh(RegistryNodeDO registryNode);

    List<RegistryNodeDO> queryForList(RegistryNodeQuery query);

    int add(RegistryNodeDO registryNode);

    List<RegistryNodeDO> findData(@Param("biz") String biz, @Param("env") String env, @Param("key") String key);

    List<RegistryNodeDO> findByRegistryId(Long registryId);

    List<RegistryNodeDO> findByRegistryIdList(List<Long> registryIdList);

    int cleanData(@Param("timeout") int timeout);

    int deleteData(@Param("biz") String biz, @Param("env") String env, @Param("key") String key);

    int deleteDataValue(@Param("biz") String biz, @Param("env") String env, @Param("key") String key, @Param("value") String value);

    int delete(Long id);

    int count();
}
