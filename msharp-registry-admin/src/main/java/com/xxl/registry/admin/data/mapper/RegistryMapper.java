package com.xxl.registry.admin.data.mapper;

import com.xxl.registry.admin.data.model.RegistryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
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
