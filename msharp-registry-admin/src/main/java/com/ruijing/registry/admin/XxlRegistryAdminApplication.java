package com.ruijing.registry.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author xuxueli 2018-11-17 17:21:44
 */
@SpringBootApplication
@ServletComponentScan
public class XxlRegistryAdminApplication {

	public static void main(String[] args) {
        SpringApplication.run(XxlRegistryAdminApplication.class, args);
	}

}