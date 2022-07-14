package com.example.springjava.securities;

import com.example.springjava.filters.AccessTokenEntryPoint;
import com.example.springjava.filters.CorsFilter;
import com.example.springjava.filters.CustomAccessTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AccessTokenEntryPoint accessTokenEntryPoint;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/login/**", "/signup/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /**
         * Cors policy
         * */
        http.addFilterBefore(corsFilter(), SessionManagementFilter.class);

        /**
         * Set current authenticated user context
         * */
        http.addFilterBefore(accessTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        /**
         * Throw message when request unauthorized
         * */
        http.exceptionHandling().authenticationEntryPoint(accessTokenEntryPoint);

        http.csrf().disable();
//        http.cors().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);


        http.authorizeRequests().antMatchers("/api/auth/login").permitAll();
        /**
         * Any request require authorize
         * */
        http.authorizeRequests().anyRequest().authenticated();

        /**
         * Custom spring security login
         * */
//        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
//        customAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");
//        http.addFilter(customAuthenticationFilter);


        /**
         * Throw message when request unauthorized
         * */
        http.exceptionHandling().authenticationEntryPoint(accessTokenEntryPoint);

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CustomAccessTokenFilter accessTokenFilter() {
        return new CustomAccessTokenFilter();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Bean()
    CorsFilter corsFilter() {
        return new CorsFilter();
    }

}
