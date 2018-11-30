package com.xxl.registry.admin.controller;

import com.xxl.registry.admin.controller.annotation.PermessionLimit;
import com.xxl.registry.admin.core.model.XxlRegistryData;
import com.xxl.registry.admin.core.result.ReturnT;
import com.xxl.registry.admin.core.util.JacksonUtil;
import com.xxl.registry.admin.service.IXxlRegistryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2018-11-29
 */
@Controller
@RequestMapping("/api")
public class ApiController {

    @Resource
    private IXxlRegistryService xxlRegistryService;


    /**
     * 服务注册 & 续约 API
     *
     * 说明：新服务注册上线1s内广播通知接入方；需要接入方循环续约，否则服务将会过期（三倍于注册中心心跳时间）下线；
     *
     * 请求参数：
     *
     *  1、业务标识：biz，RESTFUL路径参数
     *  2、环境标识：env，RESTFUL路径参数
     *  3、服务注册信息：通过post body传输，JSON格式数据，如下：
     *
     *      [{
     *          "service01" : "address01",
     *          "service02" : "address02"
     *      }]
     *
     * @param biz
     * @param env
     * @param data
     * @return
     */
    @RequestMapping("/registry/{biz}/{env}")
    @ResponseBody
    @PermessionLimit(limit=false)
    public ReturnT<String> registry(@PathVariable(value = "biz", required = false) String biz,
                                    @PathVariable(value = "env",required = false) String env,
                                    @RequestBody(required = false) String data){

        // parse
        List<XxlRegistryData> registryDataList = null;
        try {
            registryDataList = (List<XxlRegistryData>) JacksonUtil.readValue(data, List.class, XxlRegistryData.class);
        } catch (Exception e) { }

        return xxlRegistryService.registry(biz, env, registryDataList);
    }

    /**
     * 服务摘除 API
     *
     * 说明：新服务摘除下线1s内广播通知接入方；
     *
     * 请求参数：
     *
     *  1、业务标识：biz，RESTFUL路径参数
     *  2、环境标识：env，RESTFUL路径参数
     *  3、服务注册信息：通过post body传输，JSON格式数据，如下：
     *
     *      [{
     *          "service01" : "address01",
     *          "service02" : "address02"
     *      }]
     *
     * @param biz
     * @param env
     * @param data
     * @return
     */
    @RequestMapping("/remove/{biz}/{env}")
    @ResponseBody
    @PermessionLimit(limit=false)
    public ReturnT<String> remove(@PathVariable(value = "biz", required = false) String biz,
                                  @PathVariable(value = "env",required = false) String env,
                                  @RequestBody(required = false) String data){

        // parse
        List<XxlRegistryData> registryDataList = null;
        try {
            registryDataList = (List<XxlRegistryData>) JacksonUtil.readValue(data, List.class, XxlRegistryData.class);
        } catch (Exception e) { }

        return xxlRegistryService.remove(biz, env, registryDataList);
    }


    /**
     * 服务发现 API
     *
     * 说明：查询在线服务地址列表；
     *
     * 请求参数：
     *
     *  1、业务标识：biz，RESTFUL路径参数
     *  2、环境标识：env，RESTFUL路径参数
     *  3、服务注册Key列表：通过post body传输，JSON格式数据，如下：
     *
     *      [
     *          "service01",
     *          "service02"
     *      ]
     *
     * @param biz
     * @param env
     * @param data
     * @return
     */
    @RequestMapping("/discovery/{biz}/{env}")
    @ResponseBody
    @PermessionLimit(limit=false)
    public ReturnT<Map<String, List<String>>> discovery(@PathVariable(value = "biz", required = false) String biz,
                                                        @PathVariable(value = "env",required = false) String env,
                                                        @RequestBody(required = false) String data) {

        List<String> keys = JacksonUtil.readValue(data, List.class);

        return xxlRegistryService.discovery(biz, env, keys);
    }

    /**
     * 服务监控 API
     *
     * 说明：long-polling 接口，主动阻塞一段时间（三倍于注册中心心跳时间）；直至阻塞超时或服务注册信息变动时响应；
     *
     * 请求参数：
     *
     *  1、业务标识：biz，RESTFUL路径参数
     *  2、环境标识：env，RESTFUL路径参数
     *  3、服务注册Key列表：通过post body传输，JSON格式数据，如下：
     *
     *      [
     *          "service01",
     *          "service02"
     *      ]
     *
     * @param biz
     * @param env
     * @param data
     * @return
     */
    @RequestMapping("/monitor/{biz}/{env}")
    @ResponseBody
    @PermessionLimit(limit=false)
    public DeferredResult monitor(@PathVariable(value = "biz", required = false) String biz,
                                  @PathVariable(value = "env",required = false) String env,
                                  @RequestBody(required = false) String data) {

        List<String> keys = JacksonUtil.readValue(data, List.class);

        return xxlRegistryService.monitor(biz, env, keys);
    }

}
