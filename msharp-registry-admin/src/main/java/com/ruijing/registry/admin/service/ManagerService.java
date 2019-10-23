package com.ruijing.registry.admin.service;

import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.admin.response.Response;

import java.util.Map;

/**
 * ManagerService
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public interface ManagerService {

    Map<String, Object> pageList(int start, int length, String biz, String env, String key);

    Response<String> delete(long id);

    Response<String> update(RegistryDO registryDO);

    Response<String> add(RegistryDO registryDO);
}
