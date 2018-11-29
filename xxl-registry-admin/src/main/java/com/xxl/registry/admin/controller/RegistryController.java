package com.xxl.registry.admin.controller;

import com.xxl.registry.admin.core.model.XxlRegistry;
import com.xxl.registry.admin.core.result.ReturnT;
import com.xxl.registry.admin.service.IXxlRegistryService;
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
    private IXxlRegistryService xxlRegistryService;


    @RequestMapping("")
    public String index(Model model){
        return "registry/registry.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String biz,
                                        String env,
                                        String key){
        return xxlRegistryService.pageList(start, length, biz, env, key);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ReturnT<String> delete(int id){
        return xxlRegistryService.delete(id);
    }

    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(XxlRegistry xxlRegistry){
        return xxlRegistryService.update(xxlRegistry);
    }

    @RequestMapping("/add")
    @ResponseBody
    public ReturnT<String> add(XxlRegistry xxlRegistry){
        return xxlRegistryService.add(xxlRegistry);
    }




}
