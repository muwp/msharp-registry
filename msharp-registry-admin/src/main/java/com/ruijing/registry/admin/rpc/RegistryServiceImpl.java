package com.ruijing.registry.admin.rpc;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.cat.message.Transaction;
import com.ruijing.fundamental.common.collections.New;
import com.ruijing.fundamental.remoting.msharp.annotation.MSharpService;
import com.ruijing.fundamental.remoting.msharp.util.CatUtil;
import com.ruijing.registry.admin.constants.ServiceConstants;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.enums.RegistryNodeStatusEnum;
import com.ruijing.registry.admin.service.RegistryManagerService;
import com.ruijing.registry.admin.util.JsonUtil;
import com.ruijing.registry.api.dto.NodeMetaDTO;
import com.ruijing.registry.api.service.RegistryService;
import com.ruijing.registry.api.dto.RegistryNodeDTO;
import com.ruijing.registry.api.dto.RegistryNodeQueryDTO;
import com.ruijing.registry.api.request.Request;
import com.ruijing.registry.api.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@MSharpService(registry = "direct", port = ServiceConstants.SERVICE_PUBLISH_PORT)
@Service
public class RegistryServiceImpl implements RegistryService {

    private static final String CAT_TYPE = "RegistryService";

    @Autowired
    private RegistryManagerService registryService;

    @Override
    public Response<List<String>> discovery(RegistryNodeQueryDTO query) {
        Transaction transaction = Cat.newTransaction(CAT_TYPE, "discovery");
        Response<List<String>> response = null;
        try {
            response = registryService.discovery(query);
            transaction.setSuccessStatus();
        } catch (Exception ex) {
            transaction.setStatus(ex);
            transaction.addData(CatUtil.buildStackInfo(JsonUtil.toJson(query), ex));
        } finally {
            transaction.complete();
        }
        return response;
    }

    @Override
    public Response<Map<String, List<String>>> discovery(Request<RegistryNodeQueryDTO> request) {
        final Transaction transaction = Cat.newTransaction(CAT_TYPE, "discovery");
        Response<Map<String, List<String>>> response = null;
        try {
            response = registryService.discovery(request);
            transaction.setSuccessStatus();
        } catch (Exception ex) {
            transaction.addData(CatUtil.buildStackInfo(JsonUtil.toJson(request), ex));
            transaction.setStatus(ex);
        } finally {
            transaction.complete();
        }
        return response;
    }

    @Override
    public Response<Boolean> renew(Request<RegistryNodeDTO> request) {
        final Transaction transaction = Cat.newTransaction(CAT_TYPE, "renew");
        try {
            List<RegistryNodeDTO> registryNodeList = request.getList();
            final List<RegistryNodeDO> registryNodeDOList = New.listWithCapacity(registryNodeList.size());
            for (int i = 0, size = registryNodeList.size(); i < size; i++) {
                RegistryNodeDTO node = registryNodeList.get(i);
                NodeMetaDTO meta = JsonUtil.fromJson(node.getMeta(), NodeMetaDTO.class);
                RegistryNodeDO registryNodeDO = new RegistryNodeDO();
                registryNodeDO.setServiceName(node.getServiceName());
                registryNodeDO.setStatus(RegistryNodeStatusEnum.NORMAL.getCode());
                registryNodeDO.setValue(meta.toIpPortUnique());
                registryNodeDO.setAppkey(node.getAppkey());
                registryNodeDO.setEnv(node.getEnv());
                registryNodeDO.setMetric(null == node.getMetric() ? StringUtils.EMPTY : node.getMetric());
                registryNodeDO.setMeta(null == node.getMeta() ? StringUtils.EMPTY : node.getMeta());
                registryNodeDO.setVersion(null == node.getVersion() ? StringUtils.EMPTY : node.getVersion());
                registryNodeDOList.add(registryNodeDO);
            }
            return registryService.registry(registryNodeDOList);
        } catch (Exception ex) {
            transaction.addData(CatUtil.buildStackInfo(JsonUtil.toJson(request), ex));
            transaction.setStatus(ex);
        } finally {
            transaction.complete();
        }
        return new Response<>(false);
    }
}