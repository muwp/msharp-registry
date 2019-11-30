package com.ruijing.registry.admin.cache;

import cn.hutool.core.thread.NamedThreadFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.cat.message.Transaction;
import com.ruijing.registry.admin.data.mapper.TokenMapper;
import com.ruijing.registry.admin.data.model.TokenDO;
import com.ruijing.registry.admin.data.query.TokenQuery;
import com.ruijing.registry.admin.enums.TokenStatusEnum;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TokenCache
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class TokenCache implements InitializingBean {

    private static final int DEFAULT_BATCH_UPDATE_SIZE = 80;

    @Resource
    private TokenMapper tokenMapper;

    private final Cache<String, TokenDO> tokenMap = CacheBuilder.newBuilder().expireAfterWrite(80, TimeUnit.SECONDS).build();

    /**
     * 轮询服务
     */
    private final ScheduledExecutorService tokenUpdateExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("registry-token-data-update-thread", true));

    public String get(final String clientAppkey) {
        final TokenDO tokenDO = tokenMap.getIfPresent(clientAppkey);
        return null == tokenDO ? null : tokenDO.getToken();
    }

    public String syncGet(final String clientAppkey) {
        final TokenDO tokenDO = tokenMap.getIfPresent(clientAppkey);
        if (null != tokenDO) {
            return tokenDO.getToken();
        }
        final List<TokenDO> tokenDOList = tokenMapper.get(clientAppkey);
        if (CollectionUtils.isNotEmpty(tokenDOList)) {
            final TokenDO token = tokenDOList.get(0);
            tokenMap.put(token.getClientAppkey(), token);
            return token.getToken();
        }
        return null;
    }

    public int persist(final TokenDO tokenDO) {
        int updateSize = 0;
        Transaction transaction = Cat.newTransaction("TokenCache", "persist");
        try {
            updateSize = tokenMapper.insertSelective(tokenDO);
            if (updateSize > 0) {
                tokenMap.put(tokenDO.getClientAppkey(), tokenDO);
            }
        } catch (Exception ex) {
            transaction.setStatus(ex);
        } finally {
            transaction.complete();
        }
        return updateSize;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.tokenUpdateExecutor.scheduleWithFixedDelay(this::scheduledUpdateToken, 1, 60, TimeUnit.SECONDS);
    }

    public void scheduledUpdateToken() {
        try {
            int index = 0;
            boolean stop = false;
            while (!stop) {
                TokenQuery query = new TokenQuery();
                query.setPageSize(DEFAULT_BATCH_UPDATE_SIZE);
                query.setOffset(index++ * DEFAULT_BATCH_UPDATE_SIZE * 1L);
                final List<TokenDO> tokenList = tokenMapper.queryForList(query);
                if (CollectionUtils.isEmpty(tokenList)) {
                    break;
                }

                for (int i = 0, size = tokenList.size(); i < size; i++) {
                    final TokenDO tokenDO = tokenList.get(i);
                    if (tokenDO.getStatus() == null || tokenDO.getStatus() == TokenStatusEnum.DELETED.getCode()) {
                        continue;
                    }
                    this.tokenMap.put(tokenDO.getClientAppkey(), tokenDO);
                }

                if (tokenList.size() < DEFAULT_BATCH_UPDATE_SIZE) {
                    stop = true;
                }
            }
        } catch (Exception ex) {
            Cat.logError("TokenCache", "TokenCache", null, ex);
        }
    }
}
