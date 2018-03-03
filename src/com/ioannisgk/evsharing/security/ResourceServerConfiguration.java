package com.ioannisgk.evsharing.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
 
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
     
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId("my_rest_api").stateless(false);
    }
 
    // Resourse server hosts the web service resources
    // Make resource server accessible only by admins
    
    @Override
    public void configure(HttpSecurity http) throws Exception {
    	
        http
        	.anonymous().disable()
        	.requestMatchers().antMatchers("/api/**")
        	.and().authorizeRequests()
        	.antMatchers("/api/**").access("hasRole('ADMIN')")
        	.and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }
}