package com.example.certificateback.configuration;

import com.example.certificateback.security.RestAuthenticationEntryPoint;
import com.example.certificateback.security.TokenAuthenticationFilter;
import com.example.certificateback.service.implementation.UserService;
import com.example.certificateback.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        // A service used to read data about application users
        return new UserService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService()); // email
        authProvider.setPasswordEncoder(passwordEncoder());       // password
        return authProvider;
    }

    // Handler to return a 401 when a client with an incorrect username and password tries to access a resource
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Autowired
    private TokenUtils tokenUtils;

    // We define access rights for requests to specific URLs/paths
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // all unauthenticated requests uniformly process and send a 401 error
        http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint);
        http.authorizeRequests()
            .antMatchers("/api/user/login").permitAll()
			.antMatchers("/api/user/register").permitAll()
            .antMatchers("/api/user/{email}/resetPassword").permitAll()
            .antMatchers("/api/user/generateOTP").permitAll() //TODO dodati putanje kojima korisnik moze da pristupa bez autentifikacije (logovanje, registracija)

                // for every other request the user must be authenticated
                .anyRequest().authenticated().and()
                // apply configuration for CORS from WebConfig class
                .cors().and()

                // add custom filter TokenAuthenticationFilter which check JWT token
                .addFilterBefore(new TokenAuthenticationFilter(tokenUtils,  userDetailsService()), BasicAuthenticationFilter.class);

        http.csrf().disable();
        http.headers().frameOptions().disable();

        // authentication chaining
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }

    // method in which to define paths to ignore authentication
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // allowed POST method on route api/login, for any other type of HTTP method the error is 401 Unauthorized
        return (web) -> web.ignoring().antMatchers(HttpMethod.POST, "api/login", "api/user/register")
        // we allow access to resources to speed up the access process
        .antMatchers(HttpMethod.GET, "/", "/webjars/**", "/*.html", "favicon.ico",
                "/**/*.html", "/**/*.css", "/**/*.js");

    }
}
