package com.broker.social_companion_system.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSocketSecurity
@EnableWebSecurity
public class WebSocketSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        //return new BCryptPasswordEncoder();
        Map<String,PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        return new DelegatingPasswordEncoder("bcrypt", encoders);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        return new ProviderManager(provider);
    }

    @Bean
    public UserDetailsManager userDetailsService() {
        UserDetails client1 = User.builder()
                .username("client1")
                .password(passwordEncoder().encode("password1"))
                .roles("USER")
                .build();
        UserDetails client2 = User.builder()
                .username("client2")
                .password(passwordEncoder().encode("password2"))
                .roles("USER")
                .build();

        UserDetails robot1 = User.builder()
                .username("1111")
                .password(passwordEncoder().encode("rpassword1"))
                .roles("ROBOT")
                .build();

        UserDetails operator1 = User.builder()
                .username("operator1")
                .password(passwordEncoder().encode("opassword1"))
                .roles("OPERATOR")
                .build();

        UserDetails visualization = User.builder()
                .username("visualization")
                .password(passwordEncoder().encode("visualizationpass"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(client1, client2, robot1, operator1, visualization);
    }

    @Bean
    ChannelInterceptor csrfChannelInterceptor() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                return message;
            }
        };
    }

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .nullDestMatcher().authenticated()
                .simpSubscribeDestMatchers("/user/queue/errors").permitAll()
                .anyMessage().authenticated();
        //.simpDestMatchers("/app/**").permitAll();//.hasRole("USER");
        //.simpSubscribeDestMatchers("/user/**", "/topic/friends/*").hasRole("USER")
        //.simpTypeMatchers(MESSAGE, SUBSCRIBE).denyAll()
        //.anyMessage().denyAll();

        return messages.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/broker/**", "/app/**").permitAll()
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
