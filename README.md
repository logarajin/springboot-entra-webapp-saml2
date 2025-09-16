package com.example.demo.config;

import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.saml2.provider.service.authentication.OpenSamlAuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationException;
import org.springframework.security.saml2.provider.service.authentication.Saml2Error;
import org.springframework.security.saml2.provider.service.authentication.Saml2ErrorCodes;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationToken;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {
        String metadataUrl =
            "https://login.microsoftonline.com/{tenant-id}/federationmetadata/2007-06/federationmetadata.xml?appid={client-id}";

        RelyingPartyRegistration registration = RelyingPartyRegistrations
                .fromMetadataLocation(metadataUrl)
                .registrationId("azure")
                .build();

        return new InMemoryRelyingPartyRegistrationRepository(registration);
    }

    @Bean
    public OpenSamlAuthenticationProvider openSamlAuthenticationProvider() {
        OpenSamlAuthenticationProvider provider = new OpenSamlAuthenticationProvider();

        // 👇 Customize validation
        provider.setResponseAuthenticationConverter((Saml2AuthenticationToken token) -> {
            Saml2Authentication authentication =
                    OpenSamlAuthenticationProvider.createDefaultResponseAuthenticationConverter()
                            .convert(token);

            Response response = (Response) token.getResponse();

            // ✅ Require Assertion signed
            boolean assertionSigned = response.getAssertions().stream()
                    .allMatch(Assertion::isSigned);

            if (!assertionSigned) {
                throw new Saml2AuthenticationException(
                        new Saml2Error(Saml2ErrorCodes.INVALID_SIGNATURE,
                                "Assertion must be signed"));
            }

            // ❌ Don’t require Response signed (skip check)
            return authentication;
        });

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OpenSamlAuthenticationProvider samlProvider) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .saml2Login(withDefaults -> {})
            .authenticationProvider(samlProvider);

        return http.build();
    }
}
