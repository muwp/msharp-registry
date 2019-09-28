package com.ruijing.registry.admin.service.impl;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.common.collections.New;
import com.ruijing.registry.admin.manager.RegistryManager;
import com.ruijing.registry.admin.service.RegistryService;
import com.ruijing.registry.common.http.Separator;
import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.manager.RegistryDeferredCacheManager;
import com.ruijing.registry.admin.model.ReturnT;
import com.ruijing.registry.admin.util.JacksonUtil;
import com.ruijing.registry.admin.util.JsonUtils;
import com.ruijing.registry.admin.data.mapper.RegistryMapper;
import com.ruijing.registry.admin.data.mapper.RegistryNodeMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xuxueli 2016-5-28 15:30:33
 */
@Service
public class RegistryServiceImpl implements RegistryService {

    @Resource
    private RegistryMapper registryMapper;

    @Resource
    private RegistryNodeMapper registryNodeMapper;

    @Autowired
    private RegistryManager registryManager;

    @Autowired
    private RegistryDeferredCacheManager deferredResultCache;

    @Value("${msharp.registry.data.filepath}")
    private String registryDataFilePath;

    @Value("${msharp.registry.beattime}")
    private int registryBeatTime;

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
                    List<RegistryNodeDO> registryNodeDOList = registryNodeMapper.findData(registryDO.getBiz(), registryDO.getEnv(), registryDO.getKey());
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
    public ReturnT<String> delete(int id) {
        RegistryDO registryDO = registryMapper.loadById(id);
        if (null == registryDO) {
            return ReturnT.SUCCESS;
        }

        final List<RegistryNodeDO> list = registryNodeMapper.findData(registryDO.getBiz(), registryDO.getEnv(), registryDO.getKey());
        if (CollectionUtils.isNotEmpty(list)) {
            registryManager.removeRegistryNode(list);
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> update(RegistryDO registryDO) {

        // valid
        if (StringUtils.isBlank(registryDO.getBiz())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "业务线格式非空");
        }

        if (StringUtils.isBlank(registryDO.getEnv())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "环境格式非空");
        }

        if (StringUtils.isBlank(registryDO.getKey())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "注册Key非空");
        }

        if (StringUtils.isBlank(registryDO.getData())) {
            registryDO.setData(JacksonUtil.writeValueAsString(new ArrayList<String>()));
        }

        final List<String> valueList = JsonUtils.parseList(registryDO.getData(), String.class);

        if (CollectionUtils.isEmpty(valueList)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "注册Value数据格式非法；限制为字符串数组JSON格式，如 [address,address2]");
        }

        // valid exist
        final RegistryDO exist = registryMapper.loadById(registryDO.getId());
        if (exist == null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "ID参数非法");
        }

        registryDO.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
        registryMapper.update(registryDO);

        final List<RegistryNodeDO> registryNodeList = New.listWithCapacity(valueList.size());
        for (int i = 0, size = valueList.size(); i < size; i++) {
            final RegistryNodeDO registryNode = new RegistryNodeDO();
            registryNode.setValue(valueList.get(i));
            registryNode.setEnv(registryDO.getEnv());
            registryNode.setKey(registryDO.getKey());
            registryNode.setBiz(registryDO.getBiz());
            registryNodeList.add(registryNode);
        }

        if (CollectionUtils.isNotEmpty(registryNodeList)) {
            registryManager.addRegistryNode(registryNodeList);
        }

        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> add(RegistryDO registryDO) {

        // valid
        if (StringUtils.isBlank(registryDO.getBiz())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "业务线格式非空");
        }

        if (StringUtils.isBlank(registryDO.getKey())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "注册Key非空");
        }

        if (StringUtils.isBlank(registryDO.getEnv())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "环境格式非空");
        }

        if (StringUtils.isBlank(registryDO.getData())) {
            registryDO.setData(JacksonUtil.writeValueAsString(Collections.emptyList()));
        }

        List<String> valueList = JsonUtils.parseList(registryDO.getData(), String.class);
        if (valueList == null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "注册Value数据格式非法；限制为字符串数组JSON格式，如 [address,address2]");
        }

        // valid exist
        final RegistryDO exist = registryMapper.load(registryDO.getBiz(), registryDO.getEnv(), registryDO.getKey());
        if (exist != null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "注册Key请勿重复");
        }

        final List<RegistryNodeDO> registryNodeDOList = new ArrayList<>();
        for (int i = 0, size = valueList.size(); i < size; i++) {
            final RegistryNodeDO registryNode = new RegistryNodeDO();
            registryNode.setBiz(registryDO.getBiz());
            registryNode.setEnv(registryDO.getEnv());
            registryNode.setKey(registryDO.getKey());
            registryNode.setValue(valueList.get(i));
            registryNode.setUpdateTime(new Date());
            registryNodeDOList.add(registryNode);
        }

        if (CollectionUtils.isNotEmpty(registryNodeDOList)) {
            this.registryManager.addRegistryNode(registryNodeDOList);
        }
        return ReturnT.SUCCESS;
    }

    // ------------------------ remote registry ------------------------

    @Override
    public ReturnT<String> registry(String accessToken, List<RegistryNodeDO> registryNodeList) {
        // valid
        if (StringUtils.isNotBlank(this.accessToken) && !this.accessToken.equals(accessToken)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "AccessToken Invalid");
        }
        if (CollectionUtils.isEmpty(registryNodeList)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "Registry DataList Invalid");
        }
        this.registryManager.addRegistryNode(registryNodeList);
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> remove(String accessToken, List<RegistryNodeDO> registryNodeList) {

        // valid
        if (this.accessToken != null && this.accessToken.trim().length() > 0 && !this.accessToken.equals(accessToken)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "AccessToken Invalid");
        }

        if (CollectionUtils.isEmpty(registryNodeList)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "Registry DataList Invalid");
        }

        // fill + add queue
        registryManager.removeRegistryNode(registryNodeList);

        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> remove(String accessToken, RegistryNodeDO registryNode) {
        // valid
        if (this.accessToken != null && this.accessToken.trim().length() > 0 && !this.accessToken.equals(accessToken)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "AccessToken Invalid");
        }

        // fill + add queue
        registryManager.removeRegistryNode(registryNode);

        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<Map<String, List<String>>> discovery(String accessToken, String biz, String env, List<String> keys) {

        // valid
        if (StringUtils.isNotBlank(this.accessToken) && !this.accessToken.equals(accessToken)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "AccessToken Invalid");
        }

        if (StringUtils.isBlank(biz)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "biz empty");
        }

        if (StringUtils.isBlank(env)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "env empty");
        }

        if (CollectionUtils.isEmpty(keys)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "keys Invalid.");
        }

        final Map<String, List<String>> result = New.mapWithCapacity(keys.size());
        for (int i = 0, size = keys.size(); i < size; i++) {
            final String key = keys.get(i);
            RegistryNodeDO xxlRegistryData = new RegistryNodeDO();
            xxlRegistryData.setBiz(biz);
            xxlRegistryData.setEnv(env);
            xxlRegistryData.setKey(key);

            List<String> dataList = new ArrayList<>();
            RegistryDO fileXxlRegistry = null;//getFileRegistryData(xxlRegistryData);
            if (fileXxlRegistry != null) {
                dataList = fileXxlRegistry.getDataList();
            }

            result.put(key, dataList);
        }

        return new ReturnT<>(result);
    }

    @Override
    public ReturnT<List<String>> discovery(String accessToken, String biz, String env, String key) {
        // valid
        if (StringUtils.isNotBlank(this.accessToken) && !this.accessToken.equals(accessToken)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "AccessToken Invalid");
        }

        if (StringUtils.isBlank(key)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "env empty");
        }

        if (StringUtils.isBlank(env)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "env empty");
        }

        if (StringUtils.isBlank(biz)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "biz empty");
        }

        RegistryDO registryDO = null;
        boolean hasException = false;
        try {
            registryDO = registryMapper.load(biz, env, key);
        } catch (Exception ex) {
            Cat.logError("Registry", "discovery", "biz:" + biz + ", env:" + env + ", key:" + key, ex);
            hasException = true;
        }

        if (hasException) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "");
        }

        if (null == registryDO) {
            return new ReturnT<>(Collections.emptyList());
        }

        if (registryDO.getStatus() == 1 || registryDO.getStatus() == 2) {
            return new ReturnT(JsonUtils.parseList(registryDO.getData(), String.class));
        }

        final List<RegistryNodeDO> registryNodeList = registryNodeMapper.findData(biz, env, key);

        if (CollectionUtils.isEmpty(registryNodeList)) {
            return new ReturnT<>(Collections.emptyList());
        }

        return new ReturnT<>(registryNodeList.stream().map(RegistryNodeDO::getValue).collect(Collectors.toList()));
    }

    @Override
    public DeferredResult<ReturnT<String>> monitor(String accessToken, String biz, String env, List<String> keys) {

        // init
        final DeferredResult deferredResult = new DeferredResult(30 * 1000L, new ReturnT<>(ReturnT.FAIL_CODE, "Monitor timeout."));

        // valid
        if (StringUtils.isNotBlank(this.accessToken) && !this.accessToken.equals(accessToken)) {
            deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "AccessToken Invalid"));
            return deferredResult;
        }

        if (StringUtils.isBlank(biz)) {
            deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "Biz Invalid[4~255]"));
            return deferredResult;
        }

        if (StringUtils.isBlank(env)) {
            deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "Env Invalid[2~255]"));
            return deferredResult;
        }

        if (CollectionUtils.isEmpty(keys)) {
            deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "keys Invalid."));
            return deferredResult;
        }

        // monitor by client
        for (int i = 0, size = keys.size(); i < size; i++) {
            final String key = keys.get(i);
            final String fileName = this.getMessageName(biz, env, key);
            deferredResult.onCompletion(new Runnable() {

                @Override
                public void run() {
                    final List<DeferredResult> deferredResults = deferredResultCache.get(fileName);
                    if (CollectionUtils.isNotEmpty(deferredResults)) {
                        Iterator<DeferredResult> iterator = deferredResults.iterator();
                        while (iterator.hasNext()) {
                            if (deferredResult == iterator.next()) {
                                iterator.remove();
                                break;
                            }
                        }
                    }
                }
            });

            deferredResultCache.add(fileName, deferredResult);
        }

        return deferredResult;
    }

    private String getMessageName(String biz, String env, String key) {
        return biz + Separator.DOT + env + Separator.DOT + key;
    }
}