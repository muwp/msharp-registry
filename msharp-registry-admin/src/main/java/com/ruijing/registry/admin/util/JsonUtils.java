package com.ruijing.registry.admin.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 简单封装Jackson
 *
 * @author mwup
 * @version 1.0
 * @created 2018/9/4 17:03
 **/
public class JsonUtils {

    private static Logger logger = LoggerFactory.getLogger(JsonMapper.class);

    /**
     * defaultMapper
     */
    private static final JsonMapper defaultMapper = new JsonMapper();

    public static JsonMapper getDefaultMapper() {
        return defaultMapper;
    }

    public static String toJson(Object obj) {
        return defaultMapper.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return defaultMapper.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, JavaType clazz) {
        return defaultMapper.fromJson(json, clazz);
    }

    public static String toJsonP(String func, Object obj) {
        return defaultMapper.toJsonP(func, obj);
    }

    public static <T> List<T> parseList(String json, Class<T> clazz) {
        JavaType collectionType = defaultMapper.constructCollectionType(List.class, clazz);
        return defaultMapper.fromJson(json, collectionType);
    }

    public static <T> Map<String, T> parseMap(String json, Class<T> clazz) {
        final JavaType type = JsonUtils.getDefaultMapper().constructMapType(Map.class, String.class, clazz);
        return defaultMapper.fromJson(json, type);
    }

    /**
     * 简单封装Jackson，实现JSON String<->Java Object的Mapper.
     * <p>
     * 封装不同的输出风格, 使用不同的builder函数创建实例.
     *
     * @author calvin
     */
    public static class JsonMapper {

        /**
         * objectmapper mapper
         */
        private ObjectMapper mapper;

        public JsonMapper() {
            this(null);
        }

        public JsonMapper(JsonInclude.Include include) {
            mapper = new ObjectMapper();
            // 设置输出时包含属性的风格
            if (include != null) {
                mapper.setSerializationInclusion(include);
            }
            // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }

        /**
         * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper,建议在外部接口中使用.
         */
        public JsonMapper nonEmptyMapper() {
            return new JsonMapper(JsonInclude.Include.NON_EMPTY);
        }

        /**
         * 创建只输出初始值被改变的属性到Json字符串的Mapper, 最节约的存储方式，建议在内部接口中使用。
         */
        public JsonMapper nonDefaultMapper() {
            return new JsonMapper(JsonInclude.Include.NON_DEFAULT);
        }

        /**
         * Object可以是POJO，也可以是Collection或数组。
         * 如果对象为Null, 返回"null".
         * 如果集合为空集合, 返回"[]".
         */
        public String toJson(Object object) {
            try {
                return mapper.writeValueAsString(object);
            } catch (IOException e) {
                logger.warn("write to json string error:" + object, e);
                return null;
            }
        }

        /**
         * 反序列化POJO或简单Collection如List《String》.
         * <p>
         * 如果JSON字符串为Null或"null"字符串, 返回Null.
         * 如果JSON字符串为"[]", 返回空集合.
         * <p>
         * 如需反序列化复杂Collection如List#MyBean#, 请使用fromJson(String, JavaType)
         *
         * @see #fromJson(String, JavaType)
         */
        public <T> T fromJson(String jsonString, Class<T> clazz) {
            if (StringUtils.isEmpty(jsonString)) {
                return null;
            }
            try {
                return mapper.readValue(jsonString, clazz);
            } catch (IOException e) {
                logger.warn("parse json string error:" + jsonString, e);
                return null;
            }
        }

        /**
         * 反序列化复杂Collection如List《Bean》, 先使用#constructCollectionType()或contructMapType()构造类型, 然后调用本函数.
         */
        @SuppressWarnings("unchecked")
        public <T> T fromJson(String jsonString, JavaType javaType) {
            if (StringUtils.isEmpty(jsonString)) {
                return null;
            }

            try {
                return (T) mapper.readValue(jsonString, javaType);
            } catch (IOException e) {
                logger.warn("parse json string error:" + jsonString, e);
                return null;
            }
        }

        /**
         * 构造Collection类型.
         */
        public JavaType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
            return mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
        }

        /**
         * 构造Map类型.
         */
        public JavaType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
            return mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
        }

        /**
         * 当JSON里只含有Bean的部分屬性時，更新一個已存在Bean，只覆蓋該部分的屬性.
         */
        public void update(String jsonString, Object object) {
            try {
                mapper.readerForUpdating(object).readValue(jsonString);
            } catch (IOException e) {
                logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
            }
        }

        /**
         * 輸出JSONP格式數據.
         */
        public String toJsonP(String functionName, Object object) {
            return toJson(new JSONPObject(functionName, object));
        }
    }
}