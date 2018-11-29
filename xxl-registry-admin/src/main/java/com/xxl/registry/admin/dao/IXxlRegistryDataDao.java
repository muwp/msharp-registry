package com.xxl.registry.admin.dao;

import com.xxl.registry.admin.core.model.XxlRegistryData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
@Mapper
public interface IXxlRegistryDataDao {


    public int refresh(@Param("xxlRegistryData") XxlRegistryData xxlRegistryData);

    public int add(@Param("xxlRegistryData") XxlRegistryData xxlRegistryData);


    public List<XxlRegistryData> findData(@Param("biz") String biz,
                                          @Param("env") String env,
                                          @Param("key") String key);

    public int cleanData(@Param("timeout") int timeout);

    public int deleteData(@Param("biz") String biz,
                          @Param("env") String env,
                          @Param("key") String key);

    public int deleteDataValue(@Param("biz") String biz,
                          @Param("env") String env,
                          @Param("key") String key,
                           @Param("value") String value);

    public int count();

}
