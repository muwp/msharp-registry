package com.ruijing.registry.admin.controller;

import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.admin.response.Response;
import com.ruijing.registry.admin.service.ManagerService;
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
    private ManagerService managerService;

    @RequestMapping("")
    public String index(Model model) {
        return "registry/registry.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start, @RequestParam(required = false, defaultValue = "10") int length, String biz, String env, String key) {
        return managerService.pageList(start, length, biz, env, key);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Response<String> delete(int id) {
        return managerService.delete(id);
    }

    @RequestMapping("/update")
    @ResponseBody
    public Response<String> update(RegistryDO registryDO) {
        return managerService.update(registryDO);
    }

    @RequestMapping("/add")
    @ResponseBody
    public Response<String> add(RegistryDO registryDO) {
        return managerService.add(registryDO);
    }
}