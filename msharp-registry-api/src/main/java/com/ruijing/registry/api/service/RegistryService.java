package com.ruijing.registry.api.service;

import com.ruijing.registry.api.dto.RegistryNodeDTO;
import com.ruijing.registry.api.dto.RegistryNodeQueryDTO;
import com.ruijing.registry.api.request.Request;
import com.ruijing.registry.api.response.Response;

import java.util.List;
import java.util.Map;

/**
 * RegistryService
 *
 * @author mwup
 * @version 1.0
 * @created 2020/01/14 17:03
 **/
public interface RegistryService {

    /**
     * 根据request查询线上结点
     *
     * @param query 待查询的结果查询条件
     * @return 返回true如果续约成功，否则返回false
     */
    Response<List<String>> discovery(RegistryNodeQueryDTO query);

    /**
     * 根据request查询线上结点
     *
     * @param request 待查询的结果查询条件
     * @return 返回true如果续约成功，否则返回false
     */
    Response<Map<String, List<String>>> discovery(Request<RegistryNodeQueryDTO> request);

    /**
     * 根据request续约指定结点
     *
     * @param request 待续约的结果查询条件
     * @return 返回true如果续约成功，否则返回false
     */
    Response<Boolean> renew(Request<RegistryNodeDTO> request);

    /**
     * 根据request下线指定结点
     *
     * @param request 待下线的结果查询条件
     * @return 返回true如果下线成功，否则返回false
     */
    Response<Boolean> offline(Request<RegistryNodeDTO> request);
}