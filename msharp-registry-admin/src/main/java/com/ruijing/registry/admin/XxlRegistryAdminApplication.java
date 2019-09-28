package com.ruijing.registry.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * RegistryServiceImpl
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@SpringBootApplication
@ServletComponentScan
public class XxlRegistryAdminApplication {

	public static void main(String[] args) {
        SpringApplication.run(XxlRegistryAdminApplication.class, args);
	}

}