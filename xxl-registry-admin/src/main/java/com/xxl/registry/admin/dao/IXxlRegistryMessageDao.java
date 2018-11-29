package com.xxl.registry.admin.dao;

import com.xxl.registry.admin.core.model.XxlRegistryMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
@Mapper
public interface IXxlRegistryMessageDao {

    public int add(@Param("xxlRegistryMessage") XxlRegistryMessage xxlRegistryMessage);

    public List<XxlRegistryMessage> findMessage(@Param("excludeIds") List<Integer> excludeIds);

    public int cleanMessage(@Param("messageTimeout") int messageTimeout);

}
