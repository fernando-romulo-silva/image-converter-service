package org.imageconverter.util;

import java.util.Objects;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class BeanUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    public static void defineContext(final ApplicationContext newContext) {

	if (Objects.isNull(context)) {
	    context = newContext;
	}
    }

    public static <T> T getBeanFrom(final Class<T> beanClass) {
	return context.getBean(beanClass);
    }

    public static <T> ObjectProvider<T> getBeanProviderFrom(final Class<T> beanClass) {
	return context.getBeanProvider(beanClass);
    }

    public static Environment getEnvironment() {
	return context.getEnvironment();
    }

    public static String getPropertyValue(final String key) {
	return context.getEnvironment().getProperty(key);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
	defineContext(applicationContext);
    }
}
