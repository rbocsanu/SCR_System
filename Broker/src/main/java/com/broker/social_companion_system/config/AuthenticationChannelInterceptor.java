package com.broker.social_companion_system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationChannelInterceptor implements ChannelInterceptor {
    private static final String USERNAME_HEADER = "login";
    private static final String PASSWORD_HEADER = "passcode";

    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationChannelInterceptor(
            final AuthenticationManager authenticationManager
            ) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    @NonNull
    public Message<?> preSend(@NonNull final Message<?> message, @NonNull MessageChannel channel) throws AuthenticationException {
        //log.info(String.valueOf(message.getHeaders()));
        //log.info(String.valueOf(SecurityContextHolder.getContext().getAuthentication()));
        //log.info(message.getHeaders());
        //log.info(String.valueOf(message.getPayload()));
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        //log.info(String.valueOf(accessor.getDetailedLogMessage(message)));
        log.info("Accessor user: " + accessor.getUser());

        if (accessor.getCommand() == StompCommand.CONNECT) {
            log.info("Connected: Auth token set");
            log.info("Headers: " + accessor.getNativeHeader("username") + " | " +
                    accessor.getNativeHeader("password"));

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    accessor.getNativeHeader("username").get(0),
                    accessor.getNativeHeader("password").get(0)
                    //Collections.singleton((GrantedAuthority) () -> "USER") // MUST provide at least one role
            );
            Authentication authenticate = authenticationManager.authenticate(token);

            // Authentication is working and is persistent between requests, try to check credentials
            // or add headers / other roles next

            accessor.setUser(authenticate);
        }

        /*
        Authentication authenticate = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

         */
        //log.info("Authorities Inter: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        /*
        if (StompCommand.CONNECT == accessor.getCommand()) {
            final String username = accessor.getFirstNativeHeader(USERNAME_HEADER);
            final String password = accessor.getFirstNativeHeader(PASSWORD_HEADER);

            final UsernamePasswordAuthenticationToken user = webSocketAuthenticatorService.getAuthenticatedOrFail(username, password);

            accessor.setUser(user);
        }
        */

        return message;


    }
}