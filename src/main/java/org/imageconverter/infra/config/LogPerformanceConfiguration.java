package org.imageconverter.infra.config;

import org.imageconverter.application.ImageConversionService;
import org.imageconverter.util.performance.PerformanceMonitor;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogPerformanceConfiguration {

    @Bean
    public Advisor performanceMonitorAdvisor(final PerformanceMonitor performanceMonitor) {

	final var pointCut = new AspectJExpressionPointcut();

	final var expression = new StringBuilder() //
			.append(performanceMonitor.createExpression(ImageConversionService.class)) //
			.toString();

	pointCut.setExpression(expression);

	return new DefaultPointcutAdvisor(pointCut, performanceMonitor);
    }
}
