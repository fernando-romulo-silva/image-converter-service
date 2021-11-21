package org.imageconverter.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class BeanUtil implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;

    public static synchronized void defineContext(final ApplicationContext newContext) {
	CONTEXT = newContext;
    }

    public static <T> T getBeanFrom(final Class<T> beanClass) {
	return CONTEXT.getBean(beanClass);
    }
    
    public static Environment getEnvironment() {
	return CONTEXT.getEnvironment();
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
	defineContext(applicationContext);
    }
}
