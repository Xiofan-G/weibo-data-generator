package com.weibo.generator.network;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static Object getBean(String beanName) {

        return applicationContext.getBean(beanName);

    }

    public static <T> T getBean(Class<T> clazz) {

        return (T) applicationContext.getBean(clazz);

    }

    public ApplicationContext getApplicationContext() {

        return applicationContext;

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        SpringUtil.applicationContext = applicationContext;

    }

}
