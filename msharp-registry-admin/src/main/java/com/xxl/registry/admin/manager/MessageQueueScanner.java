package com.xxl.registry.admin.manager;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.common.threadpool.NamedThreadFactory;
import com.ruijing.registry.common.http.Separator;
import com.xxl.registry.admin.data.mapper.MessageQueueMapper;
import com.xxl.registry.admin.data.model.MessageQueueDO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 消息扫描器
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class MessageQueueScanner implements InitializingBean {

    private static final String CAT_TYPE = "messageQueueScanner";

    @Resource
    private MessageQueueMapper messageManager;

    @Resource
    private RegistryCacheManager registryCacheManager;

    private volatile long maxSequenceId = 0;

    /**
     * 轮询服务
     */
    private final ScheduledExecutorService pullExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("pearl-message-queue-thread", true));

    @Override
    public void afterPropertiesSet() throws Exception {
        this.pullExecutor.scheduleWithFixedDelay(this::syncMessageQueue, 10, 2, TimeUnit.SECONDS);
    }

    private void syncMessageQueue() {
        try {
            final Long sequenceId = this.maxSequenceId;
            this.maxSequenceId = System.currentTimeMillis();
            final List<MessageQueueDO> messageQueueDOList = this.messageManager.getLastNewList(sequenceId);
            if (CollectionUtils.isNotEmpty(messageQueueDOList)) {
                for (int i = 0, size = messageQueueDOList.size(); i < size; i++) {
                    final MessageQueueDO queueDO = messageQueueDOList.get(i);
                    registryCacheManager.add(queueDO.getBiz() + Separator.DOT + queueDO.getEnv() + Separator.DOT + queueDO.getKey(), queueDO.getSequenceId());
                }
            }
        } catch (Exception ex) {
            Cat.logError(CAT_TYPE, "syncMessageQueue", null, ex);
        }
    }
}
