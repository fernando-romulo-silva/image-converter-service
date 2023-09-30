package org.imageconverter.infra.util;

import java.util.Objects;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Utils to retrieves spring objects to non managed objects.
 * 
 * @author Fernando Romulo da Silva
 */
@Component
public class BeanUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    /**
     * Set the spring context to retrive objects.
     * 
     * @param newContext The new context
     */
    public static void defineContext(final ApplicationContext newContext) {

	if (Objects.isNull(context)) {
	    context = newContext;
	}
    }

    /**
     * Get a bean from its type.
     * 
     * @param <T>       The type
     * @param beanClass The class type
     * @return A bean retrieved
     */
    public static <T> T getBeanFrom(final Class<T> beanClass) {
	return context.getBean(beanClass);
    }

    /**
     * Get a bean provider (factory) from its type.
     * 
     * @param <T>       The type
     * @param beanClass The class type
     * @return A bean retrieved
     */
    public static <T> ObjectProvider<T> getBeanProviderFrom(final Class<T> beanClass) {
	return context.getBeanProvider(beanClass);
    }

    /**
     * Get the context environment
     * 
     * @return A {@link Environment} object
     */
    public static Environment getEnvironment() {
	return context.getEnvironment();
    }

    /**
     * Get the property value.
     * 
     * @param key The property value
     * @return The String value property
     */
    public static String getPropertyValue(final String key) {
	return context.getEnvironment().getProperty(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
	defineContext(applicationContext);
    }
}
