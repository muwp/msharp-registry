package com.ruijing.registry.admin.service.impl;

import com.ruijing.fundamental.common.collections.New;
import com.ruijing.registry.admin.cache.RegistryCache;
import com.ruijing.registry.admin.cache.RegistryNodeCache;
import com.ruijing.registry.admin.constants.ResponseConst;
import com.ruijing.registry.admin.data.mapper.RegistryMapper;
import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.enums.RegistryStatusEnum;
import com.ruijing.registry.admin.manager.RegistryManager;
import com.ruijing.registry.admin.service.FrontManagerService;
import com.ruijing.registry.api.dto.NodeMetaDTO;
import com.ruijing.registry.admin.util.JsonUtil;
import com.ruijing.registry.api.response.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ManagerServiceImpl
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class FrontManagerServiceImpl implements FrontManagerService {

    @Resource
    private RegistryMapper registryMapper;

    @Resource
    private RegistryCache registryCache;

    @Resource
    private RegistryNodeCache registryNodeCache;

    @Autowired
    private RegistryManager registryManager;

    @Override
    public Map<String, Object> pageList(int start, int length, String biz, String env, String key) {
        // page list
        final List<RegistryDO> list = registryMapper.pageList(start, length, biz, env, key);
        int len = 0;
        if (CollectionUtils.isNotEmpty(list)) {
            len = registryMapper.pageListCount(start, length, biz, env, key);
            for (int i = 0, size = list.size(); i < size; i++) {
                final RegistryDO registryDO = list.get(i);
                if (registryDO.getStatus() == RegistryStatusEnum.LOCKED.getCode() || registryDO.getStatus() == RegistryStatusEnum.FORBID.getCode()) {
                    //
                } else {
                    final List<RegistryNodeDO> registryNodeDOList = registryNodeCache.get(registryDO.getId());
                    if (CollectionUtils.isNotEmpty(registryNodeDOList)) {
                        List<NodeMetaDTO> result = registryNodeDOList.stream().map(RegistryNodeDO::getMeta).map((x) -> JsonUtil.fromJson(x, NodeMetaDTO.class)).collect(Collectors.toList());
                        registryDO.setData(JsonUtil.toJson(result));
                    } else {
                        registryDO.setData(JsonUtil.toJson(Collections.emptyList()));
                        registryDO.setStatus(RegistryStatusEnum.OFFLINE.getCode());
                    }
                }
            }
        }

        // package result
        final Map<String, Object> result = New.mapWithCapacity(3);

        // 总记录数
        result.put("recordsTotal", len);
        // 过滤后的总记录数
        result.put("recordsFiltered", len);
        // 分页列表
        result.put("data", list);

        return result;
    }

    @Override
    public Response<Boolean> delete(long id) {
        RegistryDO registryDO = registryCache.get(id);
        if (null == registryDO) {
            return ResponseConst.SUCCESS;
        }

        registryCache.remove(registryDO.getId());

        final List<RegistryNodeDO> list = registryNodeCache.get(registryDO.getId());
        registryManager.removeRegistryNodeList(list);
        return ResponseConst.SUCCESS;
    }

    @Override
    public Response<Boolean> update(RegistryDO registryDO) {
        final Response<Boolean> response = valid(registryDO.getAppkey(), registryDO.getEnv(), registryDO.getServiceName());
        if (null != response) {
            return response;
        }

        if (StringUtils.isBlank(registryDO.getData())) {
            registryDO.setData(JsonUtil.toJson(Collections.emptyList()));
        }

        final List<NodeMetaDTO> valueList = JsonUtil.parseList(registryDO.getData(), NodeMetaDTO.class);

        if (CollectionUtils.isEmpty(valueList)) {
            return new Response<>(Response.FAIL_CODE, "注册Value数据格式非法；限制为字符串数组JSON格式，如 [address,address2]");
        }

        // valid exist
        final RegistryDO exist = this.registryCache.get(registryDO.getId());
        if (exist == null) {
            return new Response<>(Response.FAIL_CODE, "ID参数非法");
        }

        registryDO.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
        this.registryCache.refresh(registryDO);

        final List<RegistryNodeDO> registryNodeList = New.listWithCapacity(valueList.size());
        for (int i = 0, size = valueList.size(); i < size; i++) {
            final NodeMetaDTO serviceMeta = valueList.get(i);
            final RegistryNodeDO registryNode = new RegistryNodeDO();
            registryNode.setMeta(JsonUtil.toJson(serviceMeta));
            registryNode.setEnv(registryDO.getEnv());
            registryNode.setServiceName(registryDO.getServiceName());
            registryNode.setAppkey(registryDO.getAppkey());
            registryNode.setVersion(StringUtils.EMPTY);
            registryNode.setMetric(StringUtils.EMPTY);
            registryNode.setValue(serviceMeta.toIpPortUnique());
            registryNodeList.add(registryNode);
        }

        registryManager.addRegistryNodeList(registryNodeList);
        return ResponseConst.SUCCESS;
    }

    public Response<Boolean> valid(String appkey, String env, String serviceName) {
        if (StringUtils.isBlank(serviceName)) {
            return new Response<>(Response.FAIL_CODE, "注册Key非空");
        }
        if (StringUtils.isBlank(appkey)) {
            return new Response<>(Response.FAIL_CODE, "业务线格式非空");
        }

        if (StringUtils.isBlank(env)) {
            return new Response<>(Response.FAIL_CODE, "环境格式非空");
        }
        return null;
    }

    @Override
    public Response<Boolean> add(RegistryDO registryDO) {
        Response<Boolean> response = valid(registryDO.getAppkey(), registryDO.getEnv(), registryDO.getServiceName());
        if (null != response) {
            return response;
        }

        if (StringUtils.isBlank(registryDO.getData())) {
            registryDO.setData(JsonUtil.toJson(Collections.emptyList()));
        }

         List<NodeMetaDTO> valueList = JsonUtil.parseList(registryDO.getData(), NodeMetaDTO.class);


         if (valueList == null) {
            return new Response<>(Response.FAIL_CODE, "注册Value数据格式非法；限制为字符串数组JSON格式，如 [address,address2]");
        }

        // valid exist
        final RegistryDO exist = registryCache.get(Triple.of(registryDO.getAppkey(), registryDO.getEnv(), registryDO.getServiceName()));

        if (exist != null) {
            return new Response<>(Response.FAIL_CODE, "注册Key请勿重复");
        }

        final List<RegistryNodeDO> registryNodeDOList = New.listWithCapacity(valueList.size());
        for (int i = 0, size = valueList.size(); i < size; i++) {
            final NodeMetaDTO serviceMeta = valueList.get(i);
            final RegistryNodeDO registryNode = new RegistryNodeDO();
            registryNode.setAppkey(registryDO.getAppkey());
            registryNode.setEnv(registryDO.getEnv());
            registryNode.setServiceName(registryDO.getServiceName());
            registryNode.setValue(serviceMeta.toIpPortUnique());
            registryNode.setVersion(StringUtils.EMPTY);
            registryNode.setMetric(StringUtils.EMPTY);
            registryNode.setMeta(JsonUtil.toJson(serviceMeta));
            registryNode.setUpdateTime(new Date());
            registryNodeDOList.add(registryNode);
        }

        this.registryManager.addRegistryNodeList(registryNodeDOList);
        return ResponseConst.SUCCESS;
    }
}