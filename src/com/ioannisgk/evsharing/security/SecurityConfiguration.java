package com.ioannisgk.evsharing.security;

import java.util.List;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import com.ioannisgk.evsharing.entities.Administrator;
import com.ioannisgk.evsharing.services.AdministratorService;
import com.ioannisgk.evsharing.utils.StrongTextEncryptorHelper;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	// Inject the administrator service
	@Autowired
	private AdministratorService administratorService;
		
	// Inject the text encryptor helper
	@Autowired
	private StrongTextEncryptorHelper strongTextEncryptorHelper;
	
	// Inject the client details service
	@Autowired
    private ClientDetailsService clientDetailsService;
	
	// Bean that holds all authenticated users + new ones during runtime
	@Bean
	public InMemoryUserDetailsManager inMemoryUserDetailsManager() {

        final Properties users = new Properties();
        
        // Get all administrators from the service
        List<Administrator> theAdministrators = administratorService.getAdministrators();
		
		// Iterate list and save current username, password and role
		
		for (int i = 0; i < theAdministrators.size(); i++) {
			
			// Get current attributes from administrator object
			
			String currentUsername = theAdministrators.get(i).getUsername();
			String encryptedPassword = theAdministrators.get(i).getPassword();
			String currentRole = theAdministrators.get(i).getRole();
			
			// Decrypt current password
			String currentPassword = strongTextEncryptorHelper.decryptPassword(encryptedPassword);
			
			// Authenticate all administrators in memory and grant them their roles
			users.put(currentUsername, currentPassword + ", " + "ROLE_" + currentRole + ", " + "enabled");
		}
		
		// Return authenticated users + new ones during runtime
        return new InMemoryUserDetailsManager(users);
    }
	
	@Autowired
	public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
		
		// Authenticate administrators in memory, with user details service
		auth.userDetailsService(inMemoryUserDetailsManager());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		// Make login page and oauth token accessible without authentication
		// Protect all pages and allow only admins and moderators to access them
		
		http.csrf().disable()
			.authorizeRequests()		
			.antMatchers("/home/login-page").permitAll()
			.antMatchers("/oauth/token").permitAll()
		    .antMatchers("/home/**", "/admin/**", "/user/**", "/route/**", "/vehicle/**", "/station/**", "/simulation/**", "/charts/**")
		    .access("hasAnyRole('ADMIN', 'MODERATOR')")
		    .and()
		    .formLogin()
		    .loginPage("/home/login-page")
		    .defaultSuccessUrl("/home/main", true)
		    .failureUrl("/home/login-page?fail=Invalid+login+details")
		    .and()
		    .logout().logoutSuccessUrl("/home/login-page")
		    .and()
		    .httpBasic().disable();
	}
	
	// Create a client that can request a token so he can access the web service
	
	@Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
        .withUser("evsharingUser").password("evsharingPass").roles("ADMIN");
    }
	
	// Token store and OAuth2 configuration methods
	
	@Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
	
    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }
 
    @Bean
    @Autowired
    public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore){
        TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
        handler.setTokenStore(tokenStore);
        handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
        handler.setClientDetailsService(clientDetailsService);
        return handler;
    }
     
    @Bean
    @Autowired
    public ApprovalStore approvalStore(TokenStore tokenStore) throws Exception {
        TokenApprovalStore store = new TokenApprovalStore();
        store.setTokenStore(tokenStore);
        return store;
    }
}