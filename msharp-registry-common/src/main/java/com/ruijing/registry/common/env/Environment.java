package com.ruijing.registry.common.env;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * 机器运行的环境变量
 *
 * @author mwup
 * @version 1.0
 * @created 2018/08/23 13:51
 **/
public class Environment {

    /**
     * 环境配制文件
     */
    private static final String EVN_CONFIG = "/data/env/app.env";

    /**
     * 应用配制文件
     */
    private static final String APP_EVN_CONFIG = "META-INF/app.properties";

    /**
     * 应用配制文件(spring boot [application.properties]
     */
    private static final String APPLICATION_EVN_CONFIG = "application.properties";

    /**
     * 默认的环境变量
     */
    public static final String DEFAULT_DEPLOY_ENV = "dev";

    /**
     * test 配制中心【基于http协议】
     */
    public static final String DEFAULT_TEST_CONFIG_CENTER_URL = "http://192.168.2.200:8080/pearl-server";

    /**
     * online配制中心【基于http协议】
     */
    public static final String DEFAULT_ON_LINE_CONFIG_CENTER_URL = "http://pearl.rjmart.cn/pearl-server/";

    /**
     * pearl test 配制中心 【基于tcp协议】
     */
    private static final String DEFAULT_PEARL_TEST_CONFIG_CENTER_URL = "192.168.2.200:41000";

    /**
     * pearl online配制中心 【基于tcp协议】
     */
    private static final String DEFAULT_PEARL_ON_LINE_CONFIG_CENTER_URL = "172.18.197.90:41000,172.18.197.89:41000";

    /**
     * 线上注册中心
     */
    public static final String DEFAULT_ON_REGISTRY_CENTER = "http://pearl.rjmart.cn:8081/msharp-admin";

    /**
     * 线下注册中心
     */
    public static final String DEFAULT_OFF_REGISTRY_CENTER = "http://192.168.2.200:8081/msharp-admin";

    /**
     * 发布的环境变量
     */
    public static final String KEY_DEPLOY_ENV = "deployenv";

    public static final String REGISTER_CENTER = "registrycenter";

    private static final String APPKEY = "appkey";

    /**
     * 配制中心(基于http)
     */
    public static final String CONFIG_CENTER = "configcenter";

    /**
     * pearl 配制中心(基于tpc)
     */
    public static final String PEARL_CONFIG_CENTER = "pearlconfigcenter";

    /**
     * 线上环境
     */
    public static final String ENV_PROD = "prod";

    /**
     * 线上非公环境
     */
    public static final String ENV_NON_PUBLIC_PROD = "non_public_prod";

    /**
     * 线上环境(线上)
     */
    public static final String ENV_PRODUCT = "product";

    /**
     * stag环境(线上测试）
     */
    public static final String ENV_STAGING = "stag";

    /**
     * gray环境(线上灰度）
     */
    public static final String ENV_GRAY = "gray";

    /**
     * demo环境(线上演示)
     */
    public static final String ENV_DEMO = "demo";

    /**
     * 测试环境(test,test1,test2,test3....)
     */
    public static final String ENV_TEST = "test";

    /**
     * UAT环境
     */
    public static final String ENV_UAT = "uat";

    /**
     * 线下环境
     */
    public static final String ENV_DEV = "dev";

    /**
     * 环境变量值
     */
    private static Properties DEFAULT_PROPERTY = new Properties();

    /**
     * APP 名称
     */
    private static String APP_KEY;

    /**
     * 项目步属环境
     */
    private static String DEPLOY_ENV;

    /**
     * 配制中心url(基于http)
     */
    private static String CONFIG_CENTER_URL;

    /**
     * 配制中心url(基于tcp)
     */
    private static String PEARL_CONFIG_CENTER_URL;

    /**
     * 注册中心
     */
    private static String REGISTER_CENTER_URL;

    static {
        loadAppEnv();
        APP_KEY = DEFAULT_PROPERTY.getProperty(APPKEY);
        APP_KEY = StringUtils.trimToNull(APP_KEY);
        DEPLOY_ENV = DEFAULT_PROPERTY.getProperty(KEY_DEPLOY_ENV);
        CONFIG_CENTER_URL = DEFAULT_PROPERTY.getProperty(CONFIG_CENTER);
        PEARL_CONFIG_CENTER_URL = DEFAULT_PROPERTY.getProperty(PEARL_CONFIG_CENTER);
        REGISTER_CENTER_URL = DEFAULT_PROPERTY.getProperty(REGISTER_CENTER);
        System.out.println(String.format("MSharp-Config[loaded appenv:  env [%s],appkey [%s],configcenter [%s],registrycenter[%s] ]", DEPLOY_ENV, APP_KEY, CONFIG_CENTER_URL, REGISTER_CENTER_URL));
    }

    public static String getProperty(final String key) {
        return DEFAULT_PROPERTY.getProperty(key);
    }

    public static void setProperty(final String key, final String value) {
        DEFAULT_PROPERTY.setProperty(key, value);
    }

    public static String getSystemProperty(final String key) {
        return System.getProperty(key);
    }

    public static String getSystemProperty(final String key, final String value) {
        return System.getProperty(key, value);
    }

    public static String getProperty(final String key, final String defaultValue) {
        return DEFAULT_PROPERTY.getProperty(key, defaultValue);
    }

    public static String getAppKey() {
        return APP_KEY;
    }

    public static void setAppKey(String appKey) {
        APP_KEY = appKey;
    }

    public static String getEnv() {
        return DEPLOY_ENV;
    }

    public static String getConfigCenter() {
        return CONFIG_CENTER_URL;
    }

    public static String getPearlConfigCenter() {
        return PEARL_CONFIG_CENTER_URL;
    }

    public static boolean isOnlineEnv() {
        return isProductEnv() || isStagingEnv() || isGrayEnv() || isDemoEnv() || isNonPublicProdEnv();
    }

    public static boolean isOfflineEnv() {
        return !isOnlineEnv();
    }

    public static boolean isDevEnv() {
        return ENV_DEV.equalsIgnoreCase(DEPLOY_ENV);
    }

    public static boolean isDemoEnv() {
        return ENV_DEMO.equalsIgnoreCase(DEPLOY_ENV);
    }

    public static boolean isTestEnv() {
        return ENV_TEST.equalsIgnoreCase(DEPLOY_ENV);
    }

    public static boolean isStagingEnv() {
        return ENV_STAGING.equalsIgnoreCase(DEPLOY_ENV);
    }

    public static boolean isUATEnv() {
        return ENV_UAT.equalsIgnoreCase(DEPLOY_ENV);
    }

    public static boolean isGrayEnv() {
        return ENV_GRAY.equalsIgnoreCase(DEPLOY_ENV);
    }

    public static boolean isProductEnv() {
        return ENV_PRODUCT.equalsIgnoreCase(DEPLOY_ENV) || ENV_PROD.equalsIgnoreCase(DEPLOY_ENV);
    }

    public static boolean isNonPublicProdEnv() {
        return ENV_NON_PUBLIC_PROD.equalsIgnoreCase(DEPLOY_ENV);
    }

    public static String getRegistryCenter() {
        return REGISTER_CENTER_URL;
    }

    public static boolean contains(String key) {
        return DEFAULT_PROPERTY.contains(key);
    }

    private static Properties loadAppEnv() {
        String loadFile = null;
        Properties props = loadApp();
        if (MapUtils.isNotEmpty(props)) {
            DEFAULT_PROPERTY.putAll(props);
        }
        Properties tmp = props;
        try {
            loadFile = EVN_CONFIG;
            // load from /data/configs/env/appenv
            props = PropertiesUtils.loadFromFileSystem(loadFile);
            if (props == null) {
                props = PropertiesUtils.loadFromClassPath(loadFile);
            }
        } catch (Throwable e) {
            if (tmp == props) {
                props = null;
            }
            System.err.println("failed to load data from " + loadFile);
        }

        if (null != props) {
            DEFAULT_PROPERTY.putAll(props);
        }

        final String appkey = System.getProperty(APPKEY);
        if (StringUtils.isNotBlank(appkey)) {
            DEFAULT_PROPERTY.put(APPKEY, appkey);
        }

        final String deployEnv = System.getProperty(KEY_DEPLOY_ENV);
        if (StringUtils.isNotBlank(deployEnv)) {
            DEFAULT_PROPERTY.put(KEY_DEPLOY_ENV, deployEnv);
        }

        props = getDefaultAppEnv();
        if (MapUtils.isNotEmpty(props)) {
            DEFAULT_PROPERTY.putAll(props);
        }
        return props;
    }

    private static Properties loadApp() {
        String loadFile = null;
        Properties props = null;

        try {
            // load from META-INF/app.properties
            loadFile = APP_EVN_CONFIG;
            props = PropertiesUtils.loadFromClassPath(loadFile);
        } catch (Throwable e) {
            System.err.println("failed to load data from " + loadFile);
        }

        if (null != props && props.containsKey(KEY_DEPLOY_ENV)) {
            props.remove(KEY_DEPLOY_ENV);
        }

        if (null == props) {
            props = new Properties();
        }

        // load from application.properties
        loadFile = APPLICATION_EVN_CONFIG;
        final Properties springBootProps = getProperties(loadFile);
        if (MapUtils.isNotEmpty(springBootProps)) {
            if (springBootProps.containsKey(KEY_DEPLOY_ENV)) {
                springBootProps.remove(KEY_DEPLOY_ENV);
            }
            props.putAll(springBootProps);
        }

        return props;
    }

    private static Properties getProperties(final String app) {
        Properties properties = null;
        try {
            // load from application.properties
            properties = PropertiesUtils.loadFromClassPath(app);
        } catch (Throwable e) {
            System.err.println("failed to load data from " + app);
        }
        return properties;
    }


    private static Properties getDefaultAppEnv() {
        final Properties props = new Properties();
        //设置发布环境
        if (!DEFAULT_PROPERTY.containsKey(KEY_DEPLOY_ENV)) {
            props.put(KEY_DEPLOY_ENV, DEFAULT_DEPLOY_ENV);
        }

        /**
         * 设置zk ip list
         */
        final String env = (String) DEFAULT_PROPERTY.get(KEY_DEPLOY_ENV);

        /**
         * 设置config center domain(基于http)
         */
        if (!DEFAULT_PROPERTY.containsKey(CONFIG_CENTER)) {
            if (online(env)) {
                props.put(CONFIG_CENTER, DEFAULT_ON_LINE_CONFIG_CENTER_URL);
            } else {
                props.put(CONFIG_CENTER, DEFAULT_TEST_CONFIG_CENTER_URL);
            }
        }

        /**
         * 设置config center domain(基于tcp)
         */
        if (!DEFAULT_PROPERTY.containsKey(PEARL_CONFIG_CENTER)) {
            if (online(env)) {
                props.put(PEARL_CONFIG_CENTER, DEFAULT_PEARL_ON_LINE_CONFIG_CENTER_URL);
            } else {
                props.put(PEARL_CONFIG_CENTER, DEFAULT_PEARL_TEST_CONFIG_CENTER_URL);
            }
        }

        /**
         * 设置registry center domain
         */
        if (!DEFAULT_PROPERTY.containsKey(REGISTER_CENTER)) {
            if (online(env)) {
                props.put(REGISTER_CENTER, DEFAULT_ON_REGISTRY_CENTER);
            } else {
                props.put(REGISTER_CENTER, DEFAULT_OFF_REGISTRY_CENTER);
            }
        }
        return props;
    }

    public static boolean online(final String env) {
        if (ENV_PRODUCT.equalsIgnoreCase(env) || ENV_PROD.equalsIgnoreCase(env) || ENV_GRAY.equalsIgnoreCase(env) || ENV_DEMO.equalsIgnoreCase(env) || ENV_NON_PUBLIC_PROD.equalsIgnoreCase(env)) {
            return true;
        }
        return false;
    }
}