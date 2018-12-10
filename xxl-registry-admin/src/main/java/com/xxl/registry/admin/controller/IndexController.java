package com.xxl.registry.admin.controller;

import com.xxl.registry.admin.controller.annotation.PermessionLimit;
import com.xxl.registry.admin.controller.interceptor.MPermissionInterceptor;
import com.xxl.registry.admin.core.result.ReturnT;
import com.xxl.registry.admin.dao.IXxlRegistryDao;
import com.xxl.registry.admin.dao.IXxlRegistryDataDao;
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
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
public class IndexController {


    @Resource
    private IXxlRegistryDao xxlRegistryDao;
    @Resource
    private IXxlRegistryDataDao xxlRegistryDataDao;


    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) {

        int registryNum = xxlRegistryDao.pageListCount(0, 1, null, null, null);
        int registryDataNum = xxlRegistryDataDao.count();

        model.addAttribute("registryNum", registryNum);
        model.addAttribute("registryDataNum", registryDataNum);

        return "index";
    }

    @RequestMapping("/toLogin")
    @PermessionLimit(limit = false)
    public String toLogin(Model model, HttpServletRequest request) {
        if (MPermissionInterceptor.ifLogin(request)) {
            return "redirect:/";
        }
        return "login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    @PermessionLimit(limit = false)
    public ReturnT<String> loginDo(HttpServletRequest request, HttpServletResponse response, String userName, String password, String ifRemember) {
        // valid
        if (MPermissionInterceptor.ifLogin(request)) {
            return ReturnT.SUCCESS;
        }

        // param
        if (userName == null || userName.trim().length() == 0 || password == null || password.trim().length() == 0) {
            return new ReturnT<String>(500, "请输入账号密码");
        }
        boolean ifRem = (ifRemember != null && "on".equals(ifRemember)) ? true : false;

        // do login
        boolean loginRet = MPermissionInterceptor.login(response, userName, password, ifRem);

        if (!loginRet) {
            return new ReturnT<String>(500, "账号密码错误");
        }
        return ReturnT.SUCCESS;
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    @PermessionLimit(limit = false)
    public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response) {
        if (MPermissionInterceptor.ifLogin(request)) {
            MPermissionInterceptor.logout(request, response);
        }
        return ReturnT.SUCCESS;
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
