package com.ruijing.registry.admin.service;

import com.ruijing.registry.admin.data.model.RegistryNodeDO;

public interface MessageQueueService {

      void sendMessageQueue(RegistryNodeDO registryNode);
}
