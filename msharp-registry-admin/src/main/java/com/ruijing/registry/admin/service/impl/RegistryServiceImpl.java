package com.ruijing.registry.admin.service.impl;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.cat.message.Transaction;
import com.ruijing.fundamental.common.builder.JsonObjectBuilder;
import com.ruijing.fundamental.common.collections.New;
import com.ruijing.registry.admin.cache.RegistryCache;
import com.ruijing.registry.admin.cache.RegistryNodeCache;
import com.ruijing.registry.admin.manager.RegistryManager;
import com.ruijing.registry.admin.model.Response;
import com.ruijing.registry.admin.service.RegistryService;
import com.ruijing.registry.admin.util.KeyUtil;
import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.manager.RegistryDeferredCacheManager;
import com.ruijing.registry.admin.util.JsonUtils;
import com.ruijing.registry.admin.data.mapper.RegistryMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RegistryServiceImpl
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class RegistryServiceImpl implements RegistryService {

    private static final Response<List<String>> EMPTY_RETURN_LIST = new Response<>(Collections.emptyList());

    @Resource
    private RegistryMapper registryMapper;

    @Resource
    private RegistryCache registryCache;

    @Resource
    private RegistryNodeCache registryNodeCache;

    @Autowired
    private RegistryManager registryManager;

    @Autowired
    private RegistryDeferredCacheManager deferredResultCache;

    @Value("${msharp.registry.accessToken}")
    private String accessToken;

    @Override
    public Map<String, Object> pageList(int start, int length, String biz, String env, String key) {
        // page list
        final List<RegistryDO> list = registryMapper.pageList(start, length, biz, env, key);
        int len = 0;
        if (CollectionUtils.isNotEmpty(list)) {
            len = registryMapper.pageListCount(start, length, biz, env, key);
            for (int i = 0, size = list.size(); i < size; i++) {
                final RegistryDO registryDO = list.get(i);
                if (registryDO.getStatus() == 1 || registryDO.getStatus() == 2) {
                    //
                } else {
                    final List<RegistryNodeDO> registryNodeDOList = registryNodeCache.get(registryDO.getId());
                    if (CollectionUtils.isNotEmpty(registryNodeDOList)) {
                        List<String> result = registryNodeDOList.stream().map(RegistryNodeDO::getValue).collect(Collectors.toList());
                        registryDO.setDataList(result);
                        registryDO.setData(JsonUtils.toJson(result));
                    } else {
                        registryDO.setData(JsonUtils.toJson(Collections.emptyList()));
                        registryDO.setStatus(3);
                    }
                }
            }
        }

        // package result
        final Map<String, Object> result = new HashMap<String, Object>((int) (3 / 0.75));

        // 总记录数
        result.put("recordsTotal", len);
        // 过滤后的总记录数
        result.put("recordsFiltered", len);
        // 分页列表
        result.put("data", list);
        return result;
    }

    @Override
    public Response<String> delete(long id) {
        RegistryDO registryDO = registryCache.get(id);
        if (null == registryDO) {
            return Response.SUCCESS;
        }

        final List<RegistryNodeDO> list = registryNodeCache.get(registryDO.getId());
        registryManager.removeRegistryNodeList(list);
        return Response.SUCCESS;
    }

    @Override
    public Response<String> update(RegistryDO registryDO) {

        // valid
        if (StringUtils.isBlank(registryDO.getBiz())) {
            return new Response<>(Response.FAIL_CODE, "业务线格式非空");
        }

        if (StringUtils.isBlank(registryDO.getEnv())) {
            return new Response<>(Response.FAIL_CODE, "环境格式非空");
        }

        if (StringUtils.isBlank(registryDO.getKey())) {
            return new Response<>(Response.FAIL_CODE, "注册Key非空");
        }

        if (StringUtils.isBlank(registryDO.getData())) {
            registryDO.setData(JsonUtils.toJson(Collections.emptyList()));
        }

        final List<String> valueList = JsonUtils.parseList(registryDO.getData(), String.class);

        if (CollectionUtils.isEmpty(valueList)) {
            return new Response<>(Response.FAIL_CODE, "注册Value数据格式非法；限制为字符串数组JSON格式，如 [address,address2]");
        }

        // valid exist
        final RegistryDO exist = this.registryCache.get(registryDO.getId());
        if (exist == null) {
            return new Response<>(Response.FAIL_CODE, "ID参数非法");
        }

        registryDO.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
        this.registryMapper.update(registryDO);

        final List<RegistryNodeDO> registryNodeList = New.listWithCapacity(valueList.size());
        for (int i = 0, size = valueList.size(); i < size; i++) {
            final RegistryNodeDO registryNode = new RegistryNodeDO();
            registryNode.setValue(valueList.get(i));
            registryNode.setEnv(registryDO.getEnv());
            registryNode.setKey(registryDO.getKey());
            registryNode.setBiz(registryDO.getBiz());
            registryNodeList.add(registryNode);
        }

        registryManager.addRegistryNodeList(registryNodeList);

        return Response.SUCCESS;
    }

    @Override
    public Response<String> add(RegistryDO registryDO) {

        // valid
        if (StringUtils.isBlank(registryDO.getBiz())) {
            return new Response<>(Response.FAIL_CODE, "业务线格式非空");
        }

        if (StringUtils.isBlank(registryDO.getKey())) {
            return new Response<>(Response.FAIL_CODE, "注册Key非空");
        }

        if (StringUtils.isBlank(registryDO.getEnv())) {
            return new Response<>(Response.FAIL_CODE, "环境格式非空");
        }

        if (StringUtils.isBlank(registryDO.getData())) {
            registryDO.setData(JsonUtils.toJson(Collections.emptyList()));
        }

        List<String> valueList = JsonUtils.parseList(registryDO.getData(), String.class);
        if (valueList == null) {
            return new Response<>(Response.FAIL_CODE, "注册Value数据格式非法；限制为字符串数组JSON格式，如 [address,address2]");
        }

        // valid exist
        final RegistryDO exist = registryCache.get(Triple.of(registryDO.getBiz(), registryDO.getEnv(), registryDO.getKey()));

        if (exist != null) {
            return new Response<>(Response.FAIL_CODE, "注册Key请勿重复");
        }

        final List<RegistryNodeDO> registryNodeDOList = New.listWithCapacity(valueList.size());
        for (int i = 0, size = valueList.size(); i < size; i++) {
            final RegistryNodeDO registryNode = new RegistryNodeDO();
            registryNode.setBiz(registryDO.getBiz());
            registryNode.setEnv(registryDO.getEnv());
            registryNode.setKey(registryDO.getKey());
            registryNode.setValue(valueList.get(i));
            registryNode.setUpdateTime(new Date());
            registryNodeDOList.add(registryNode);
        }

        this.registryManager.addRegistryNodeList(registryNodeDOList);
        return Response.SUCCESS;
    }

    // ------------------------ remote registry ------------------------

    @Override
    public Response<String> registry(String accessToken, List<RegistryNodeDO> registryNodeList) {
        // valid
        if (StringUtils.isNotBlank(this.accessToken) && !this.accessToken.equals(accessToken)) {
            return new Response<>(Response.FAIL_CODE, "AccessToken Invalid");
        }
        this.registryManager.addRegistryNodeList(registryNodeList);
        return Response.SUCCESS;
    }

    @Override
    public Response<String> remove(String accessToken, List<RegistryNodeDO> registryNodeList) {

        // valid
        if (this.accessToken != null && this.accessToken.trim().length() > 0 && !this.accessToken.equals(accessToken)) {
            return new Response<>(Response.FAIL_CODE, "AccessToken Invalid");
        }

        if (CollectionUtils.isEmpty(registryNodeList)) {
            return new Response<>(Response.FAIL_CODE, "Registry DataList Invalid");
        }

        // fill + add queue
        registryManager.removeRegistryNodeList(registryNodeList);

        return Response.SUCCESS;
    }

    @Override
    public Response<String> remove(final String accessToken, final RegistryNodeDO registryNode) {
        // valid
        if (this.accessToken != null && this.accessToken.trim().length() > 0 && !this.accessToken.equals(accessToken)) {
            return new Response<>(Response.FAIL_CODE, "AccessToken Invalid");
        }

        // fill + add queue
        registryManager.removeRegistryNode(registryNode);

        return Response.SUCCESS;
    }

    @Override
    public Response<Map<String, List<String>>> discovery(String accessToken, String biz, String env, List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return new Response<>(Collections.emptyMap());
        }

        // valid
        if (StringUtils.isNotBlank(this.accessToken) && !this.accessToken.equals(accessToken)) {
            return new Response<>(Response.FAIL_CODE, "AccessToken Invalid");
        }

        if (StringUtils.isBlank(biz)) {
            return new Response<>(Response.FAIL_CODE, "biz empty");
        }

        if (StringUtils.isBlank(env)) {
            return new Response<>(Response.FAIL_CODE, "env empty");
        }

        if (CollectionUtils.isEmpty(keys)) {
            return new Response<>(Response.FAIL_CODE, "keys Invalid.");
        }

        final Map<String, List<String>> result = New.mapWithCapacity(keys.size());
        for (int i = 0, size = keys.size(); i < size; i++) {
            final String key = keys.get(i);
            Response<List<String>> returnT = this.discovery(accessToken, biz, env, key);
            if (returnT.getCode() == Response.SUCCESS_CODE) {
                result.put(key, returnT.getData());
            }
        }

        return new Response<>(result);
    }

    @Override
    public Response<List<String>> discovery(String accessToken, String biz, String env, String key) {
        // valid
        if (StringUtils.isNotBlank(this.accessToken) && !this.accessToken.equals(accessToken)) {
            return new Response<>(Response.FAIL_CODE, "AccessToken Invalid");
        }

        if (StringUtils.isBlank(key)) {
            return new Response<>(Response.FAIL_CODE, "env empty");
        }

        if (StringUtils.isBlank(env)) {
            return new Response<>(Response.FAIL_CODE, "env empty");
        }

        if (StringUtils.isBlank(biz)) {
            return new Response<>(Response.FAIL_CODE, "biz empty");
        }

        final RegistryDO registryDO = registryCache.get(biz, env, key);

        if (null == registryDO) {
            return EMPTY_RETURN_LIST;
        }

        if (registryDO.getStatus() == 1 || registryDO.getStatus() == 2) {
            return new Response(JsonUtils.parseList(registryDO.getData(), String.class));
        }

        final List<RegistryNodeDO> registryNodeList = this.registryNodeCache.get(registryDO.getId());

        if (CollectionUtils.isEmpty(registryNodeList)) {
            Cat.logEvent("discovery", JsonObjectBuilder.custom().put("biz", biz).put("env", env).put("key", key).build().toString(), Transaction.ERROR, "");
            return EMPTY_RETURN_LIST;
        }

        return new Response<>(registryNodeList.stream().map(RegistryNodeDO::getValue).collect(Collectors.toList()));
    }

    @Override
    public DeferredResult<Response<String>> monitor(String accessToken, String biz, String env, List<String> keys) {

        // init
        final DeferredResult deferredResult = new DeferredResult(30 * 1000L, new Response<>(Response.FAIL_CODE, "Monitor timeout."));

        // valid
        if (StringUtils.isNotBlank(this.accessToken) && !this.accessToken.equals(accessToken)) {
            deferredResult.setResult(new Response<>(Response.FAIL_CODE, "AccessToken is empty"));
            return deferredResult;
        }

        if (StringUtils.isBlank(biz)) {
            deferredResult.setResult(new Response<>(Response.FAIL_CODE, "Biz is empty"));
            return deferredResult;
        }

        if (StringUtils.isBlank(env)) {
            deferredResult.setResult(new Response<>(Response.FAIL_CODE, "Env is empty"));
            return deferredResult;
        }

        if (CollectionUtils.isEmpty(keys)) {
            deferredResult.setResult(new Response<>(Response.FAIL_CODE, "keys is empty"));
            return deferredResult;
        }

        // monitor by client
        for (int i = 0, size = keys.size(); i < size; i++) {
            final String key = keys.get(i);
            final String fileName = KeyUtil.getKey(biz, env, key);
            deferredResult.onCompletion(() -> this.clearDeferredResult(key, deferredResult));
            deferredResultCache.add(fileName, deferredResult);
        }
        return deferredResult;
    }

    private void clearDeferredResult(String key, DeferredResult deferredResult) {
        final List<DeferredResult> deferredResults = deferredResultCache.get(key);
        if (CollectionUtils.isEmpty(deferredResults)) {
            return;
        }
        Iterator<DeferredResult> iterator = deferredResults.iterator();
        while (iterator.hasNext()) {
            if (deferredResult == iterator.next()) {
                iterator.remove();
                break;
            }
        }
    }
}