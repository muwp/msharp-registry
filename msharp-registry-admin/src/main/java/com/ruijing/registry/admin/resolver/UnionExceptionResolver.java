package com.ruijing.registry.admin.resolver;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.registry.api.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * UnionExceptionResolver
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Component
public class UnionExceptionResolver implements HandlerExceptionResolver {

    private static transient Logger logger = LoggerFactory.getLogger(UnionExceptionResolver.class);

    private static final String CAT_TYPE = "UnionExceptionResolver";

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        logger.error("MqExceptionResolver:", ex);
        Cat.logError(CAT_TYPE, "resolveException", null, ex);

        // if json
        boolean isJson = false;
        HandlerMethod method = (HandlerMethod) handler;
        ResponseBody responseBody = method.getMethodAnnotation(ResponseBody.class);
        if (responseBody != null) {
            isJson = true;
        }

        // error result
        Response<String> errorResult = new Response<String>(Response.FAIL_CODE, ex.toString().replaceAll("\n", "<br/>"));

        // response
        ModelAndView mv = new ModelAndView();
        if (isJson) {
            try {
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().print("{\"code\":" + errorResult.getCode() + ", \"msg\":\"" + errorResult.getMsg() + "\"}");
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                Cat.logError(CAT_TYPE, "print", null, e);
            }
            return mv;
        } else {
            mv.addObject("exceptionMsg", errorResult.getMsg());
            mv.setViewName("/common/common.exception");
            return mv;
        }
    }

}