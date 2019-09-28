package com.ruijing.registry.admin.test;

import com.ruijing.registry.admin.util.JacksonUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Test {


    public static void main(String[] args) {

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "success");
        result.put("arr", Arrays.asList("111","222"));
        result.put("float", 1.11f);

        System.out.println(JacksonUtil.writeValueAsString(Integer.valueOf(111)));
        System.out.println(JacksonUtil.writeValueAsString(String.valueOf("111")));
        System.out.println(JacksonUtil.writeValueAsString(result));

    }

}
