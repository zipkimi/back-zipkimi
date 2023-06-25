package com.zipkimi.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.GET, path = "/health")
public @interface GetHealthMapping {
}
