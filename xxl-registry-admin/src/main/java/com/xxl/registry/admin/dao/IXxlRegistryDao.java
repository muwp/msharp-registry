package com.xxl.registry.admin.dao;

import com.xxl.registry.admin.core.model.XxlRegistry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
@Mapper
public interface IXxlRegistryDao {

    public List<XxlRegistry> pageList(@Param("offset") int offset,
                                      @Param("pagesize") int pagesize,
                                      @Param("biz") String biz,
                                      @Param("env") String env,
                                      @Param("key") String key);
    public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("biz") String biz,
                             @Param("env") String env,
                             @Param("key") String key);

    public XxlRegistry load(@Param("biz") String biz,
                            @Param("env") String env,
                            @Param("key") String key);

    public XxlRegistry loadById(@Param("id") int id);

    public int add(@Param("xxlRegistry") XxlRegistry xxlRegistry);

    public int update(@Param("xxlRegistry") XxlRegistry xxlRegistry);

    public int delete(@Param("id") int id);

}
