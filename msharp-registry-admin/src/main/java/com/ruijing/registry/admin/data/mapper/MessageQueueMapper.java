package com.ruijing.registry.admin.data.mapper;

import com.ruijing.registry.admin.data.model.MessageQueueDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MessageQueueMapper
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Mapper
public interface MessageQueueMapper {

    Long insertSelective(MessageQueueDO queueDO);

    MessageQueueDO selectByPrimaryKey(Long id);

    List<MessageQueueDO> queryForList(@Param("biz") String biz, @Param("env") String env, @Param("key") String key);

    List<MessageQueueDO> listAll();

    int updateByPrimaryKeySelective(MessageQueueDO queueDO);

    List<MessageQueueDO> getLastNewList(Long sequenceId);

}
