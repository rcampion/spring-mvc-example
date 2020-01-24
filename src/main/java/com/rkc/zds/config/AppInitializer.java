package com.rkc.zds.config;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;
import org.springframework.web.servlet.DispatcherServlet;

import com.rkc.zds.config.security.SecurityConfiguration;
//import com.rkc.zds.config.security.hmac.HmacSecurityConfigurer;
//import com.rkc.zds.config.security.hmac.HmacSecurityConfigurer;

@Configuration
@EnableWebMvc
@EnableWebSecurity
@ComponentScan(basePackages = { "com.rkc.zds"  })
public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
// public class AppInitializer extends AbstractDispatcherServletInitializer {
// public class AppInitializer implements WebApplicationInitializer {
/*
    @Override
    public void onStartup(ServletContext container) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();

        context.scan("com.rkc.zds");

        container.addListener(new ContextLoaderListener(context));

        ServletRegistration.Dynamic dispatcher = container.addServlet("mvc", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
*/    
	@Bean
	public SecurityContextRepository securityContextRepository() {
	    return new HttpSessionSecurityContextRepository();
	}

	/**
	 * Gets the security context persistence filter.
	 *
	 * @return the security context persistence filter
	 */
	@Bean(name = "sif")
	public SecurityContextPersistenceFilter getSecurityContextPersistenceFilter() {
		return new SecurityContextPersistenceFilter(securityContextRepository());
	}

	@Bean(name = "filterChainProxy")
	public FilterChainProxy getFilterChainProxy() {

		SecurityFilterChain chain = new DefaultSecurityFilterChain(new AntPathRequestMatcher("/*"),
				getSecurityContextPersistenceFilter());
		return new FilterChainProxy(chain);

	}

/*	
	@Override
	protected javax.servlet.Filter[] getServletFilters() {
	    DelegatingFilterProxy delegateFilterProxy = new DelegatingFilterProxy();
	    delegateFilterProxy.setTargetBeanName("applicationFilter");
	    return new Filter[]{delegateFilterProxy};
	}
*/
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		servletContext.addFilter("springSecurityFilterChain", new DelegatingFilterProxy("springSecurityFilterChain"))
				.addMappingForUrlPatterns(null, false, "/*");

		servletContext.getServletRegistration("default").addMapping("/static/*", "*.html", "*.ico");

		servletContext.addListener(HttpSessionEventPublisher.class);
		super.onStartup(servletContext);
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return null;
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { AppConfig.class, WebAppConfig.class, 
				SecurityConfiguration.class};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

}