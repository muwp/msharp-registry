package com.ruijing.registry.admin.cache;

import cn.hutool.core.thread.NamedThreadFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.cat.message.Transaction;
import com.ruijing.registry.admin.data.mapper.TokenMapper;
import com.ruijing.registry.admin.data.model.TokenDO;
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
//@Service
public class TokenCache implements InitializingBean {

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
        this.tokenUpdateExecutor.scheduleWithFixedDelay(this::updateToken, 1, 60, TimeUnit.SECONDS);
    }

    public void updateToken() {
        try {
            final List<TokenDO> tokenDOList = tokenMapper.listBizToken();
            if (CollectionUtils.isNotEmpty(tokenDOList)) {
                for (final TokenDO tokenDO : tokenDOList) {
                    tokenMap.put(tokenDO.getClientAppkey(), tokenDO);
                }
            }
        } catch (Exception ex) {
            Cat.logError("TokenCache", "updateToken", null, ex);
        }
    }
}
