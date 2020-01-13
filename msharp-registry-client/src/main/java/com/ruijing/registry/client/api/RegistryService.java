package com.ruijing.registry.client.api;

import com.ruijing.registry.client.dto.RegistryNodeDTO;
import com.ruijing.registry.client.request.Request;
import com.ruijing.registry.client.response.Response;

public interface RegistryService {

    Response discovery();

    Response pusblish(Request<RegistryNodeDTO> request);
}