package com.example.demo;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
class MySamlLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	 
	   
	   
	   public void onAuthenticationSuccess1(HttpServletRequest request, HttpServletResponse response,
				Authentication authentication) throws IOException, ServletException {
		 
			handle(request, response, authentication);
			   System.out.println("request ===========>"+request);
			clearAuthenticationAttributes(request);
		}
	   
		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
				Authentication authentication) throws IOException, ServletException {
			//handle(request, response, authentication);
		//	String targetUrl = determineTargetUrl(request, response, authentication);
			  System.out.println("Test ======11111111111111111111111111111==targetUrl===>"+request.getContextPath());

            String username = authentication.getPrincipal().toString();
             
            System.out.println("The user " + username + " has logged in.");
             
            response.sendRedirect("http://localhost:8080/secure1");
		}

	}