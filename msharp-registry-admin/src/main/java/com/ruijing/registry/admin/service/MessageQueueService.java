package com.ruijing.registry.admin.service;

import com.ruijing.registry.admin.data.model.RegistryNodeDO;

/**
 * MessageQueueService
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public interface MessageQueueService {

    void sendMessageQueue(RegistryNodeDO registryNode);
}