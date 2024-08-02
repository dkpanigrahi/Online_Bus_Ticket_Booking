/*
 * package com.demo.config;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.context.annotation.Bean; import
 * org.springframework.context.annotation.Configuration; import
 * org.springframework.security.authentication.dao.DaoAuthenticationProvider;
 * import
 * org.springframework.security.config.annotation.web.builders.HttpSecurity;
 * import org.springframework.security.config.annotation.web.configuration.
 * EnableWebSecurity; import
 * org.springframework.security.core.userdetails.UserDetailsService; import
 * org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import
 * org.springframework.security.web.SecurityFilterChain; import
 * org.springframework.security.web.authentication.AuthenticationFailureHandler;
 * import org.springframework.security.web.authentication.
 * SimpleUrlAuthenticationFailureHandler;
 * 
 * @Configuration
 * 
 * @EnableWebSecurity public class SecurityConfig {
 * 
 * @Autowired public CustomAuthSuccessHandler successHandler;
 * 
 * @Bean public BCryptPasswordEncoder passwordEncoder() { return new
 * BCryptPasswordEncoder(); }
 * 
 * @Bean public UserDetailsService getDetailsService() { return new
 * CustomUserDetailsService(); }
 * 
 * @Bean public DaoAuthenticationProvider getAuthenticationProvider() {
 * DaoAuthenticationProvider daoAuthenticationProvider = new
 * DaoAuthenticationProvider();
 * daoAuthenticationProvider.setUserDetailsService(getDetailsService());
 * daoAuthenticationProvider.setPasswordEncoder(passwordEncoder()); return
 * daoAuthenticationProvider; }
 * 
 * @Bean public AuthenticationFailureHandler authenticationFailureHandler() {
 * SimpleUrlAuthenticationFailureHandler handler = new
 * SimpleUrlAuthenticationFailureHandler(); handler.setUseForward(false);
 * handler.setDefaultFailureUrl("/signin?error=true"); return handler; }
 * 
 * @Bean public SecurityFilterChain filterChain(HttpSecurity http) throws
 * Exception {
 * http.csrf().disable().authorizeHttpRequests().requestMatchers("/user/**").
 * hasRole("USER") .requestMatchers("/admin/**").hasRole("ADMIN")
 * .requestMatchers("/**").permitAll().and()
 * .formLogin().loginPage("/signin").loginProcessingUrl("/loginUser")
 * .successHandler(successHandler)
 * .failureHandler(authenticationFailureHandler()) // Add this line
 * .and().logout().permitAll();
 * 
 * return http.build(); } }
 */




package com.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.demo.listener.SessionListener;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthSuccessHandler successHandler;

    @Autowired
    private SessionListener sessionListener; // Ensure this bean is injected correctly

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService getDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public DaoAuthenticationProvider getAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(getDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        SimpleUrlAuthenticationFailureHandler handler = new SimpleUrlAuthenticationFailureHandler();
        handler.setUseForward(false);
        handler.setDefaultFailureUrl("/signin?error=true");
        return handler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeHttpRequests()
                .requestMatchers("/user/**").hasRole("USER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/**").permitAll()
            .and()
                .formLogin()
                    .loginPage("/signin")
                    .loginProcessingUrl("/loginUser")
                    .successHandler(successHandler)
                    .failureHandler(authenticationFailureHandler())
            .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/signin?logout") // Redirect after logout
                    .invalidateHttpSession(true) // Invalidate the session
                    .deleteCookies("JSESSIONID") // Optionally delete cookies
                    .permitAll()
            .and()
                .exceptionHandling()
                    .accessDeniedPage("/access-denied"); // Optional access denied page
        
        return http.build();
    }
}
