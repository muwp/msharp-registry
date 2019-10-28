package com.ruijing.registry.admin.data.mapper;

import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.data.query.RegistryNodeQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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

    Date getSystemDateTime();

    List<RegistryNodeDO> findData(@Param("appkey") String appkey, @Param("env") String env, @Param("serviceName") String serviceName);

    List<RegistryNodeDO> findByRegistryId(Long registryId);

    List<RegistryNodeDO> findByRegistryIdList(List<Long> registryIdList);

    int cleanData(@Param("timeout") int timeout);

    int deleteData(@Param("appkey") String appkey, @Param("env") String env, @Param("serviceName") String serviceName);

    int deleteDataValue(@Param("appkey") String appkey, @Param("env") String env, @Param("serviceName") String serviceName, @Param("value") String value);

    int removeDataValue(@Param("appkey") String appkey, @Param("env") String env, @Param("serviceName") String serviceName, @Param("value") String value);

    int delete(Long id);

    int remove(Long id);

    int count();
}
