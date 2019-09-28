package com.ruijing.registry.admin.controller;

import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.admin.model.ReturnT;
import com.ruijing.registry.admin.service.RegistryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author xuxueli 2018-11-21
 */
@Controller
@RequestMapping("/registry")
public class RegistryController {

    @Resource
    private RegistryService registryService;

    @RequestMapping("")
    public String index(Model model) {
        return "registry/registry.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start, @RequestParam(required = false, defaultValue = "10") int length, String biz, String env, String key) {
        return registryService.pageList(start, length, biz, env, key);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ReturnT<String> delete(int id) {
        return registryService.delete(id);
    }

    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(RegistryDO registryDO) {
        return registryService.update(registryDO);
    }

    @RequestMapping("/add")
    @ResponseBody
    public ReturnT<String> add(RegistryDO registryDO) {
        return registryService.add(registryDO);
    }
}
