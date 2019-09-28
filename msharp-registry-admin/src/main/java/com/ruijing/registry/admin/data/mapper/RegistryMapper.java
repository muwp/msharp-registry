package com.ruijing.registry.admin.data.mapper;

import com.ruijing.registry.admin.data.model.RegistryDO;
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

    List<RegistryDO> pageList(@Param("offset") int offset, @Param("pagesize") int pagesize, @Param("biz") String biz, @Param("env") String env, @Param("key") String key);

    int pageListCount(@Param("offset") int offset, @Param("pagesize") int pagesize, @Param("biz") String biz, @Param("env") String env, @Param("key") String key);

    RegistryDO load(@Param("biz") String biz, @Param("env") String env, @Param("key") String key);

    RegistryDO loadById(@Param("id") int id);

    int add(@Param("xxlRegistry") RegistryDO xxlRegistry);

    int update(@Param("xxlRegistry") RegistryDO xxlRegistry);

    int delete(@Param("id") int id);
}
