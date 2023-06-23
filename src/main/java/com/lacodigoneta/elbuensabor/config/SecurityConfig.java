package com.lacodigoneta.elbuensabor.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.lacodigoneta.elbuensabor.services.UserDetailsServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NamingConventions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;

import static com.lacodigoneta.elbuensabor.config.AppConstants.ORIGIN_APP;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${google.client-id}")
    private String googleClientId;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSourceNamingConvention(NamingConventions.JAVABEANS_ACCESSOR);
        mapper.getConfiguration().setDestinationNamingConvention(NamingConventions.JAVABEANS_MUTATOR);
        return mapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder, UserDetailsServiceImpl userDetailsServiceImpl) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsServiceImpl);
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier() {

        String CLIENT_ID = googleClientId;

        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        HttpTransport transport = Utils.getDefaultTransport();

        return new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(ORIGIN_APP, "http://127.0.0.1:5173")
                        .allowedMethods("GET", "POST", "PUT", "PATCH");
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authenticationManager,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        http
                .csrf().disable()
                .cors()
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/users/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .authenticationManager(authenticationManager)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
