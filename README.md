package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.saml2.provider.service.authentication.*;
import org.springframework.security.saml2.provider.service.registration.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // 1. Load MS Entra ID metadata (contains signing certs)
    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {
        String metadataUrl = "https://login.microsoftonline.com/{tenant-id}/federationmetadata/2007-06/federationmetadata.xml?appid={client-id}";

        RelyingPartyRegistration registration =
                RelyingPartyRegistrations
                        .fromMetadataLocation(metadataUrl)
                        .registrationId("azure")
                        .build();

        return new InMemoryRelyingPartyRegistrationRepository(registration);
    }

    // 2. Customize authentication provider to enforce Assertion signed
    @Bean
    public Saml2AuthenticationProvider saml2AuthenticationProvider(
            RelyingPartyRegistrationRepository registrations) {

        Saml2AuthenticationProvider provider = new Saml2AuthenticationProvider(registrations);

        provider.setResponseAuthenticationConverter((responseToken) -> {
            Saml2Authentication authentication =
                    Saml2AuthenticationConverter.withDefaults().convert(responseToken);

            // ✅ Require Assertion signed
            boolean allAssertionsSigned = responseToken.getResponse().getAssertions()
                    .stream()
                    .allMatch(assertion -> assertion.getSignature() != null);

            if (!allAssertionsSigned) {
                throw new Saml2AuthenticationException(
                        new Saml2Error(Saml2ErrorCodes.INVALID_SIGNATURE,
                                "Assertion must be signed")
                );
            }

            // ❌ Do not require Response to be signed
            return authentication;
        });

        return provider;
    }

    // 3. Standard Spring Security filter chain with SAML2 login
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            Saml2AuthenticationProvider samlProvider) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .saml2Login(withDefaults -> {})
            .authenticationProvider(samlProvider);

        return http.build();
    }
}
