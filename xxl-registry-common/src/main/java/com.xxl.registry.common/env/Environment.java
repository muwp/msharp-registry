package com.xxl.registry.common.env;

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
     * 默认的环境变量
     */
    public static final String DEFAULT_DEPLOY_ENV = "dev";

    /**
     * default offline zk server ip list
     * <p>
     * 120.78.81.219:2181,120.78.81.219:2182,120.78.81.219:2183/
     */
    public static final String DEFAULT_TEST_ZK_SERVER = "39.108.108.154:2181,39.108.188.51:2181,120.79.63.24:2181/";

    /**
     * default online zk server ip list
     */
    public static final String DEFAULT_ON_LINE_ZK_SERVER = "39.108.108.154:2181,39.108.188.51:2181,120.79.63.24:2181/";

    /**
     * test 配制中心
     */
    public static final String DEFAULT_TEST_CONFIG_CENTER_URL = "http://192.168.2.200:8080/pearl-server";

    /**
     * online配制中心
     */
    public static final String DEFAULT_ON_LINE_CONFIG_CENTER_URL = "http://pearl.rjmart.cn/pearl-server/";

    /**
     * online配制中心
     */
    public static final String DEFAULT_ON_LINE_CAT_CENTER_URL = "http://cat.rjmart.cn";

    /**
     * offline配制中心
     */
    public static final String DEFAULT_OFF_LINE_CAT_CENTER_URL = "http://192.168.2.201:8080";

    /**
     * 发布的环境变量
     */
    public static final String KEY_DEPLOY_ENV = "deployenv";

    /**
     * zk服务器列表
     */
    public static final String KEY_ZK_SERVER = "zkserver";

    /**
     * 配制中心
     */
    public static final String CONFIG_CENTER = "configcenter";

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
     * cat url variable
     */
    public static final String CAT_URL = "catUrl";

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
     * zk服务器列表
     */
    private static String ZK_SERVER;

    /**
     * 配制中心url
     */
    private static String CONFIG_CENTER_URL;

    private static String CAT_URL_ADDRESS;

    static {
        loadAppEnv();
        APP_KEY = DEFAULT_PROPERTY.getProperty("appkey");
        APP_KEY = StringUtils.trimToNull(APP_KEY);
        ZK_SERVER = DEFAULT_PROPERTY.getProperty(KEY_ZK_SERVER);
        DEPLOY_ENV = DEFAULT_PROPERTY.getProperty(KEY_DEPLOY_ENV);
        CONFIG_CENTER_URL = DEFAULT_PROPERTY.getProperty(CONFIG_CENTER);
        CAT_URL_ADDRESS = DEFAULT_PROPERTY.getProperty(CAT_URL);
        System.out.println(String.format("loaded appenv:  env [%s],appkey [%s], zkserverList [%s],configcenter [%s]", DEPLOY_ENV, APP_KEY, ZK_SERVER, CONFIG_CENTER_URL));
    }

    public static String getProperty(final String key) {
        return DEFAULT_PROPERTY.getProperty(key);
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

    public static String getZKAddress() {
        return ZK_SERVER;
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

    public static String getCatUrl() {
        return CAT_URL_ADDRESS;
    }

    public static boolean isNonPublicProdEnv() {
        return ENV_NON_PUBLIC_PROD.equalsIgnoreCase(DEPLOY_ENV);
    }

    private static Properties loadAppEnv() {
        String loadFile = null;
        Properties props;
        props = loadApp();
        if (MapUtils.isNotEmpty(props)) {
            DEFAULT_PROPERTY.putAll(props);
        }
        try {
            loadFile = EVN_CONFIG;
            // load from /data/configs/env/appenv
            props = PropertiesUtils.loadFromFileSystem(loadFile);
            if (props == null) {
                props = PropertiesUtils.loadFromClassPath(loadFile);
            }
        } catch (Throwable e) {
            System.err.println("failed to load data from " + loadFile);
        }

        if (null != props) {
            DEFAULT_PROPERTY.putAll(props);
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
        return props;
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
        if (!DEFAULT_PROPERTY.containsKey(KEY_ZK_SERVER)) {
            if (online(env)) {
                props.put(KEY_ZK_SERVER, DEFAULT_ON_LINE_ZK_SERVER);
            } else {
                props.put(KEY_ZK_SERVER, DEFAULT_TEST_ZK_SERVER);
            }
        }

        /**
         * 设置config center domain
         */
        if (!DEFAULT_PROPERTY.containsKey(CONFIG_CENTER)) {
            if (online(env)) {
                props.put(CONFIG_CENTER, DEFAULT_ON_LINE_CONFIG_CENTER_URL);
            } else {
                props.put(CONFIG_CENTER, DEFAULT_TEST_CONFIG_CENTER_URL);
            }
        }

        /**
         * 设置config center domain
         */
        if (!DEFAULT_PROPERTY.containsKey(CAT_URL)) {
            if (online(env)) {
                props.put(CAT_URL, DEFAULT_ON_LINE_CAT_CENTER_URL);
            } else {
                props.put(CAT_URL, DEFAULT_OFF_LINE_CAT_CENTER_URL);
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