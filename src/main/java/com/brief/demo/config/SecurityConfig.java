package com.brief.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;



@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true , jsr250Enabled = true)
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws  Exception{
         return  httpSecurity
                             .csrf(customiser -> customiser.disable())
                             .authorizeHttpRequests(request -> request
                                     .requestMatchers("/api/auth/**").permitAll()
                                     .requestMatchers("/api/auth/register").hasRole("ADMIN")
                                     .requestMatchers("/api/products/**").hasAnyRole("ADMIN","WAREHOUSE_MANAGER" , "CLIENT")
                                     .requestMatchers("/api/inventory/**").hasAnyRole("ADMIN","WAREHOUSE_MANAGER")
                                     .requestMatchers("/api/shipment/**").hasAnyRole("ADMIN","WAREHOUSE_MANAGER","CLIENT")
                                     .requestMatchers("/api/orders/**").hasAnyRole("ADMIN","WAREHOUSE_MANAGER","CLIENT")
                                     .requestMatchers("/api/sales-orders/**").hasRole("ADMIN")
                                     .requestMatchers("/api/backorders/**").hasRole("ADMIN")
                                     .requestMatchers("/api/purchase-orders/**").hasAnyRole("ADMIN" , "WAREHOUSE_MANAGER")
                                     .requestMatchers("/api/suppliers/**").hasAnyRole("ADMIN","WAREHOUSE_MANAGER","CLIENT")
                                     .requestMatchers("/api/warehouses/**").hasAnyRole("ADMIN","WAREHOUSE_MANAGER")
                             )

                             .oauth2ResourceServer(oauth -> oauth
                                         .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                                 )
                             .sessionManagement(session -> session
                                       .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                             .build();

    }


    @Bean
    public JwtDecoder jwtDecoder() {
        String SECRET_KEY = "my-super-secret-key-my-super-secret-key";
        SecretKey key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

            List<String> roles = jwt.getClaimAsStringList("roles");
            if(roles != null){
                roles.forEach(role ->
                        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role))
                );
            }

            List<String> permissions = jwt.getClaimAsStringList("permissions");
            if(permissions != null){
                permissions.forEach(permission ->
                        grantedAuthorities.add(new SimpleGrantedAuthority(permission))
                );
            }

            return  grantedAuthorities;
        });
        return converter;
    }



}
