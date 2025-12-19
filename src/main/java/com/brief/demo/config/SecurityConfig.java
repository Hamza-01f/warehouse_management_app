package com.brief.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import java.nio.charset.StandardCharsets;



import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;



@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
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
                                     .requestMatchers("/api/products/**").hasAnyRole("ADMIN","WAREHOUSE_MANAGER")
                                     .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                     .requestMatchers("/api/inventory/**").hasAnyRole("ADMIN","WAREHOUSE_MANAGER")
                                     .requestMatchers("/api/shipment/**").hasRole("WAREHOUSE_MANAGER")
                                     .requestMatchers("/api/orders/**").hasAnyRole("ADMIN","WAREHOUSE_MANAGER","CLIENT")
                                     .anyRequest().authenticated())
//                           .formLogin(Customizer.withDefaults())
//                             .httpBasic(Customizer.withDefaults())
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
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtConverter;
    }



}
