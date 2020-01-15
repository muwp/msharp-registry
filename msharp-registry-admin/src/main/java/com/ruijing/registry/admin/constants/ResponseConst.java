package com.ruijing.registry.admin.constants;

import com.ruijing.registry.api.response.Response;

import java.util.List;
import java.util.Map;

public class ResponseConst {

    public static final int SUCCESS_CODE = 200;

    public static final int FORBIDDEN_CODE = 403;

    public static final int FAIL_CODE = 500;

    public static final Response<Boolean> SUCCESS = new Response<>(null);

    public static final Response<Boolean> REGISTRY_FAIL = new Response<>(FAIL_CODE, null);

    public static final Response<String> FORBIDDEN = new Response<>(FORBIDDEN_CODE, null);

    public static final Response<Map<String, List<String>>> FAIL_ = new Response<>(FAIL_CODE, null);

}
