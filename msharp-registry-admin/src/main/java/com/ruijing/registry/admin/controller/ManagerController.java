package com.ruijing.registry.admin.controller;

import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.admin.service.FrontManagerService;
import com.ruijing.registry.client.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * RegistryController
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Controller
@RequestMapping("/registry")
public class ManagerController {

    @Resource
    private FrontManagerService managerService;

    @RequestMapping("")
    public String index(Model model) {
        return "registry/registry.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start, @RequestParam(required = false, defaultValue = "10") int length, String appkey, String env, String serviceName) {
        if (StringUtils.isNotBlank(serviceName)) {
            serviceName = serviceName.trim();
        }
        return managerService.pageList(start, length, appkey, env, serviceName);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Response<Boolean> delete(int id) {
        return managerService.delete(id);
    }

    @RequestMapping("/update")
    @ResponseBody
    public Response<Boolean> update(RegistryDO registryDO) {
        return managerService.update(registryDO);
    }

    @RequestMapping("/add")
    @ResponseBody
    public Response<Boolean> add(RegistryDO registryDO) {
        return managerService.add(registryDO);
    }
}