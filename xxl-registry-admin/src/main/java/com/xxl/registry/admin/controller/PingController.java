package com.xxl.registry.admin.controller;

import com.xxl.registry.admin.controller.annotation.PermessionLimit;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * index controller
 */
@RestController
@RequestMapping(value = "/")
public class PingController {

    @PermessionLimit(limit = false)
    @RequestMapping(value = "/ping")
    @ResponseBody
    public String ping() {
        return "pong";
    }
}
