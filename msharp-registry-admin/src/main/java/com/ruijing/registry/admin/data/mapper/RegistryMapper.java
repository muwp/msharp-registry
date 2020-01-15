package com.ruijing.registry.admin.data.mapper;

import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.api.dto.RegistryNodeQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * RegistryMapper
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Mapper
public interface RegistryMapper {

    List<RegistryDO> queryForList(RegistryNodeQueryDTO query);

    List<RegistryDO> pageList(@Param("offset") int offset, @Param("pagesize") int pagesize, @Param("appkey") String biz, @Param("env") String env, @Param("serviceName") String key);

    int pageListCount(@Param("offset") int offset, @Param("pagesize") int pagesize, @Param("appkey") String biz, @Param("env") String env, @Param("serviceName") String key);

    RegistryDO load(@Param("appkey") String appkey, @Param("env") String env, @Param("serviceName") String serviceName);

    RegistryDO loadById(@Param("id") Long id);

    int add(RegistryDO registryDO);

    int update(RegistryDO registryDO);

    int delete(Long id);
}
