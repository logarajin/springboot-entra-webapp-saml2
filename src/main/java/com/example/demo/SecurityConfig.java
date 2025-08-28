package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.saml2.provider.service.registration.*;
import java.util.List;
@Configuration
public class SecurityConfig {

@Autowired
MySamlLoginSuccessHandler mySamlLoginSuccessHandler;
	   @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/", "/public/**").permitAll()
	                .anyRequest().authenticated()
	            )
	           
	            .saml2Metadata(Customizer.withDefaults())
                
                .saml2Login(saml2 -> saml2.successHandler(mySamlLoginSuccessHandler))
	            ;//saml2 -> saml2.successHandler(mySamlLoginSuccessHandler)
	        return http.build();
	    }
	   
	   @Bean
	    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {
	        RelyingPartyRegistration registration = RelyingPartyRegistrations
	            .fromMetadataLocation("https://login.microsoftonline.com/4b5567ea-ae61-4536-825e-d93d069b8438/federationmetadata/2007-06/federationmetadata.xml?appid=bc647539-412a-4bfa-93f0-900ebe91b488")
	            .registrationId("azure")
	            .entityId("springboot-app")
	            .assertionConsumerServiceLocation("http://localhost:8080/login/saml2/sso/azure")
	            .build();

	        return new InMemoryRelyingPartyRegistrationRepository(List.of(registration));
	    }
}
