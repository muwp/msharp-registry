package com.ruijing.registry.admin;

import com.msharp.sharding.jdbc.springboot.autoconfigure.annotation.EnableMSharpDataSource;
import com.ruijing.cat.springboot.autoconfigure.annotation.EnableCat;
import com.ruijing.pearl.annotation.EnablePearl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * RegistryAdminApplication
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@SpringBootApplication
@ServletComponentScan
@EnableCat
@EnableMSharpDataSource
@EnablePearl
public class RegistryAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(RegistryAdminApplication.class, args);
    }
}