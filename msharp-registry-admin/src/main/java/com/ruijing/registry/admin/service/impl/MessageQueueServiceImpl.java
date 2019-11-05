package com.ruijing.registry.admin.service.impl;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.cat.message.Transaction;
import com.ruijing.registry.admin.data.mapper.MessageQueueMapper;
import com.ruijing.registry.admin.data.model.MessageQueueDO;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.service.MessageQueueService;
import com.ruijing.registry.admin.util.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * MessageQueueServiceImpl
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class MessageQueueServiceImpl implements MessageQueueService {

    @Resource
    private MessageQueueMapper messageQueueMapper;

    @Override
    public void sendMessageQueue(RegistryNodeDO registryNode) {
        final MessageQueueDO queueDO = new MessageQueueDO();
        queueDO.setAppkey(registryNode.getAppkey());
        queueDO.setEnv(registryNode.getEnv());
        queueDO.setServiceName(registryNode.getServiceName());
        try {
            final List<MessageQueueDO> list = this.messageQueueMapper.queryForList(registryNode.getAppkey(), registryNode.getEnv(), registryNode.getServiceName());
            if (CollectionUtils.isEmpty(list)) {
                final Date date = new Date();
                queueDO.setUpdateTime(date);
                queueDO.setSequenceId(System.currentTimeMillis());
                Transaction transaction = Cat.newTransaction("messageQueueMapper", "messageQueueMapper.insertSelective");
                try {
                    this.messageQueueMapper.insertSelective(queueDO);
                    transaction.setSuccessStatus();
                } catch (Exception ex) {
                    transaction.setStatus(ex);
                } finally {
                    transaction.complete();
                }
            } else {
                final MessageQueueDO messageQueueDO = new MessageQueueDO();
                final Date date = new Date();
                messageQueueDO.setUpdateTime(date);
                messageQueueDO.setSequenceId(System.currentTimeMillis());
                messageQueueDO.setId(list.get(0).getId());
                Transaction transaction = Cat.newTransaction("messageQueueMapper", "messageQueueMapper.updateByPrimaryKeySelective");
                try {
                    this.messageQueueMapper.updateByPrimaryKeySelective(messageQueueDO);
                    transaction.setSuccessStatus();
                } catch (Exception ex) {
                    transaction.setStatus(ex);
                    Cat.logError("messageQueueMapper", "syncMessageQueue", JsonUtils.toJson(messageQueueDO), ex);
                } finally {
                    transaction.complete();
                }
            }
        } catch (Exception e) {
            Cat.logError("messageQueueMapper", "syncMessageQueue", JsonUtils.toJson(registryNode), e);
        }
    }
}
