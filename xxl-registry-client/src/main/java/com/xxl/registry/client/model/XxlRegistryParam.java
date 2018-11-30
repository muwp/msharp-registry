package com.xxl.registry.client.model;

/**
 * @author xuxueli 2018-11-30
 */
public class XxlRegistryParam {

    private String key;
    private String value;

    public XxlRegistryParam() {
    }
    public XxlRegistryParam(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
