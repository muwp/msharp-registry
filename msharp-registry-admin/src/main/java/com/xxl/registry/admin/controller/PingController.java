package com.xxl.registry.admin.controller;

import com.xxl.registry.admin.annotation.PermissionLimit;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * index controller
 */
@RestController
@RequestMapping(value = "/")
public class PingController {

    @PermissionLimit(limit = false)
    @RequestMapping(value = "/ping")
    @ResponseBody
    public String ping() {
        return "pong";
    }
}
