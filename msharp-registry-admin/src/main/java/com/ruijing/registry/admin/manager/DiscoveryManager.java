package com.ruijing.registry.admin.manager;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.cat.message.Transaction;
import com.ruijing.registry.admin.cache.ClientNodeCache;
import com.ruijing.registry.admin.cache.RegistryCache;
import com.ruijing.registry.admin.data.mapper.ClientNodeMapper;
import com.ruijing.registry.admin.data.model.ClientNodeDO;
import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.admin.enums.RegistryClientNodeStatusEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;

/**
 * DiscoveryManager
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class DiscoveryManager implements InitializingBean {

    private static final long ONE_HOUR = 3600000;

    @Autowired
    private RegistryCache registryCache;

    @Autowired
    private ClientNodeCache clientNodeCache;

    @Resource
    private ClientNodeMapper clientNodeMapper;

    private volatile boolean executorStop = false;

    private final ExecutorService executorService = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5));

    private volatile LinkedBlockingQueue<ClientNodeDO> clientNodeQueue = new LinkedBlockingQueue<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        this.executorService.execute(this::scheduledSaveOrUpdateClientNode);
    }

    public boolean addClientNode(final ClientNodeDO clientNode) {
        if (null == clientNode) {
            return false;
        }
        return clientNodeQueue.add(clientNode);
    }

    private void scheduledSaveOrUpdateClientNode() {
        while (!executorStop) {
            try {
                final ClientNodeDO clientNode = clientNodeQueue.take();
                if (null == clientNode) {
                    continue;
                }

                final RegistryDO registryDO = registryCache.get(clientNode.getAppkey(), clientNode.getEnv(), clientNode.getServiceName());
                if (null == registryDO) {
                    continue;
                }

                clientNode.setRegistryId(registryDO.getId());
                clientNode.setStatus(RegistryClientNodeStatusEnum.NORMAL.getCode());

                final List<ClientNodeDO> clientNodeList = clientNodeCache.get(clientNode.getAppkey(), clientNode.getEnv(), clientNode.getServiceName());
                ClientNodeDO targetNode = this.getClientNode(clientNode.getAppkey(), clientNode.getEnv(), clientNode.getServiceName(), clientNode.getClientAppkey(), clientNodeList);
                boolean canExecute = true;
                if (targetNode != null) {
                    final Date nodeUpdateDate = targetNode.getUpdateTime();
                    targetNode.setUpdateTime(clientNode.getUpdateTime());
                    if (clientNode.getUpdateTime().getTime() - nodeUpdateDate.getTime() < ONE_HOUR) {
                        canExecute = false;
                    }
                }

                if (!canExecute) {
                    continue;
                }

                if (targetNode != null) {
                    clientNode.setId(targetNode.getId());
                }

                if (clientNode.getId() != null) {
                    this.update(clientNode);
                } else {
                    this.saveOrUpdate(clientNode);
                }
            } catch (Exception e) {
                Cat.logError("DiscoveryManager", "scheduledSaveOrUpdateClientNode", StringUtils.EMPTY, e);
            }
        }
    }

    private ClientNodeDO getClientNode(String appkey, String env, String serviceName, String clientAppkey, List<ClientNodeDO> clientNodeList) {
        if (CollectionUtils.isEmpty(clientNodeList)) {
            return null;
        }
        for (int i = 0, size = clientNodeList.size(); i < size; i++) {
            final ClientNodeDO nodeDO = clientNodeList.get(i);
            if (nodeDO.getAppkey().equalsIgnoreCase(appkey) && nodeDO.getServiceName().equalsIgnoreCase(serviceName) && nodeDO.getEnv().equalsIgnoreCase(env) && nodeDO.getClientAppkey().equalsIgnoreCase(clientAppkey)) {
                return nodeDO;
            }
        }
        return null;
    }

    private void update(final ClientNodeDO nodeDO) {
        Transaction transaction = Cat.newTransaction("clientNodeMapper", "clientNodeMapper.updateByPrimaryKeySelective");
        try {
            clientNodeMapper.updateByPrimaryKeySelective(nodeDO);
            transaction.setSuccessStatus();
        } catch (Exception ex) {
            transaction.setStatus(ex);
        } finally {
            transaction.complete();
        }
    }

    private int saveOrUpdate(final ClientNodeDO nodeDO) {
        int updateSize = 0;
        Transaction transaction = Cat.newTransaction("clientNodeMapper", "clientNodeMapper.insertSelective");
        try {
            updateSize = clientNodeMapper.refresh(nodeDO);
            if (updateSize <= 0) {
                updateSize = clientNodeMapper.insertSelective(nodeDO);
            }
            transaction.setSuccessStatus();
        } catch (Exception ex) {
            transaction.setStatus(ex);
        } finally {
            transaction.complete();
        }

        return updateSize;
    }
}