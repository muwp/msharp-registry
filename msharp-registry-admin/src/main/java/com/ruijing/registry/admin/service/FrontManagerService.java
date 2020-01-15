package com.ruijing.registry.admin.service;

import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.client.response.Response;

import java.util.Map;

/**
 * FrontManagerService
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public interface FrontManagerService {

    Map<String, Object> pageList(int start, int length, String biz, String env, String key);

    Response<Boolean> delete(long id);

    Response<Boolean> update(RegistryDO registryDO);

    Response<Boolean> add(RegistryDO registryDO);
}