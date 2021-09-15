package org.imageconverter.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class BeansController implements ApplicationContextAware {
    private ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

    // https://www.baeldung.com/spring-show-all-beans
    @GetMapping(path = {"/", "/beans"}, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String,String> allBeans() {
        Map<String,String> map = new HashMap<>();
        Arrays.stream(ctx.getBeanDefinitionNames()).forEach(beanName -> {
            map.put(beanName, ctx.getBean(beanName).getClass().toString());
        });
        return map;
    }
}