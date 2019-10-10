package com.ruijing.registry.admin.controller;

import com.ruijing.registry.admin.annotation.PermissionLimit;
import com.ruijing.registry.admin.cache.AppKeyCache;
import com.ruijing.registry.admin.model.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * AppkeyController
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Controller
public class AppkeyController {

    private AppKeyCache appKeyCache;

    @RequestMapping("/appkey/valid")
    @PermissionLimit(limit = false)
    public Response<Boolean> valid(@RequestParam("appkey") String appkey) {
        boolean contains = this.appKeyCache.contains(appkey);
        return new Response<>(contains);
    }
}