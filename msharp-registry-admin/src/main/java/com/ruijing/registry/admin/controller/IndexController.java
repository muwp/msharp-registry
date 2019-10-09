package com.ruijing.registry.admin.controller;

import com.ruijing.registry.admin.annotation.PermissionLimit;
import com.ruijing.registry.admin.data.mapper.RegistryMapper;
import com.ruijing.registry.admin.filter.PermissionInterceptorAdapter;
import com.ruijing.registry.admin.model.Response;
import com.ruijing.registry.admin.data.mapper.RegistryNodeMapper;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * IndexController
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Controller
public class IndexController {

    @Resource
    private RegistryMapper xxlRegistryDao;

    @Resource
    private RegistryNodeMapper xxlRegistryDataDao;

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) {
        int registryNum = xxlRegistryDao.pageListCount(0, 1, null, null, null);
        int registryDataNum = xxlRegistryDataDao.count();
        model.addAttribute("registryNum", registryNum);
        model.addAttribute("registryDataNum", registryDataNum);
        return "index";
    }

    @RequestMapping("/toLogin")
    @PermissionLimit(limit = false)
    public String toLogin(Model model, HttpServletRequest request) {
        if (PermissionInterceptorAdapter.ifLogin(request)) {
            return "redirect:/";
        }
        return "login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    @PermissionLimit(limit = false)
    public Response<String> loginDo(HttpServletRequest request, HttpServletResponse response, String userName, String password, String ifRemember) {
        // valid
        if (PermissionInterceptorAdapter.ifLogin(request)) {
            return Response.SUCCESS;
        }

        // param
        if (userName == null || userName.trim().length() == 0 || password == null || password.trim().length() == 0) {
            return new Response<>(500, "请输入账号密码");
        }
        boolean ifRem = (ifRemember != null && "on".equals(ifRemember)) ? true : false;

        // do login
        boolean loginRet = PermissionInterceptorAdapter.login(response, userName, password, ifRem);

        if (!loginRet) {
            return new Response<>(500, "账号密码错误");
        }
        return Response.SUCCESS;
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    @PermissionLimit(limit = false)
    public Response<String> logout(HttpServletRequest request, HttpServletResponse response) {
        if (PermissionInterceptorAdapter.ifLogin(request)) {
            PermissionInterceptorAdapter.logout(request, response);
        }
        return Response.SUCCESS;
    }

    @RequestMapping("/help")
    public String help() {
        return "help";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
}