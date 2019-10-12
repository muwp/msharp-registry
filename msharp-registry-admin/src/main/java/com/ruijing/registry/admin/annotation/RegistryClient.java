package com.ruijing.registry.admin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RegistryClient
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistryClient {

    boolean token() default true;
}