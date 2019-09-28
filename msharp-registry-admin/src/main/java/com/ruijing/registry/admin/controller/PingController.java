package com.ruijing.registry.admin.controller;

import com.ruijing.registry.admin.annotation.PermissionLimit;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * PingController
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
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
