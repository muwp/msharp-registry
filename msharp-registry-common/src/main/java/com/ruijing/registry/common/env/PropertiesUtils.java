package com.ruijing.registry.common.env;


import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 环境配制属性工具类
 *
 * @author mwup
 * @version 1.0
 * @created 2018/08/23 13:53
 **/
public class PropertiesUtils {

    private static final String SCHEMA_CLASSPATH = "classpath:";

    private static final String SCHEMA_FILE = "file:";

    public static Properties load(String resource) throws IOException {
        if (resource == null) {
            throw new NullPointerException("resource is null");
        }
        if (resource.startsWith(SCHEMA_CLASSPATH)) {
            return loadFromClassPath(resource.substring(SCHEMA_CLASSPATH.length()));
        } else if (resource.startsWith(SCHEMA_FILE)) {
            return loadFromFileSystem(resource.substring(SCHEMA_FILE.length()));
        } else {
            Properties props = loadFromClassPath(resource);
            if (props == null) {
                props = loadFromFileSystem(resource);
            }
            return props;
        }
    }

    public static Properties loadFromFileSystem(String file) throws IOException {
        File f = new File(file);
        if (!f.exists()) {
            return null;
        }
        URL url = f.toURI().toURL();
        return load(url);
    }

    public static Properties loadFromClassPath(String file) throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(file);
        if (url == null) {
            return null;
        }
        return load(url);
    }

    public static Properties load(URL url) throws IOException {
        InputStream in = null;
        try {
            in = url.openStream();
            Properties props = new Properties();
            props.load(in);
            return props;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static void toMap(final Map<String, Object> map, final Map<String, String> result, final StringBuilder builder) {
        if (MapUtils.isEmpty(map)) {
            return;
        }
        final String parentKey = builder.toString();
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if (value instanceof Map) {
                if (StringUtils.isBlank(parentKey)) {
                    toMap((Map) value, result, new StringBuilder(key));
                } else {
                    toMap((Map) value, result, new StringBuilder(builder).append(".").append(key));
                }
            } else {
                if (StringUtils.isBlank(parentKey)) {
                    result.put(key, String.valueOf(value));
                } else {
                    result.put(parentKey + "." + key, String.valueOf(value));
                }
            }
        }
    }

    public static void save(Map<String, String> properties, String fileName) throws IOException {
        if (properties == null) {
            throw new NullPointerException("properties is null");
        }
        if (fileName == null) {
            throw new NullPointerException("file is null");
        }
        File file = new File(fileName);
        if (!file.exists()) {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            Properties props = new Properties();
            props.putAll(properties);
            props.store(writer, null);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static String get(Map<String, String> properties, String key, String defaultValue) {
        if (properties == null) {
            return null;
        }
        String value = properties.get(key);
        return value == null ? defaultValue : value;
    }

    public static Boolean getBoolean(Map<String, String> properties, String key, Boolean defaultValue) {
        if (properties == null) {
            return null;
        }
        String value = properties.get(key);
        return value == null ? defaultValue : Boolean.valueOf(value);
    }
}

