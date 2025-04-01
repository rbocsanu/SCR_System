package com.broker.social_companion_system.config;

import com.broker.social_companion_system.client.ClientService;
import com.broker.social_companion_system.operator.OperatorManagementService;
import com.broker.social_companion_system.server.ServerManagementService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;

@Slf4j
@AllArgsConstructor
@Service
public class ConnectionEventHandler {

    private final UserDetailsService userDetailsService;
    private final ServerManagementService serverManagementService;
    private final ClientService clientService;
    private final OperatorManagementService operatorManagementService;

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) event.getUser();
        Collection<GrantedAuthority> authorities = user.getAuthorities();
        log.info("User disconnected: " + authorities);

        boolean isServer = false;
        boolean isClient = false;
        boolean isOperator = false;

        for (GrantedAuthority authority : authorities) {
            isServer = authority.getAuthority().equals("ROLE_SERVER");
            isClient = authority.getAuthority().equals("ROLE_CLIENT");
            isOperator = authority.getAuthority().equals("ROLE_OPERATOR");
        }

        if (isServer) {
            serverManagementService.serverDisconnect(user.getName());
        }
        if (isClient) {
            clientService.clientDisconnected(user.getName());
        }
        if (isOperator) {
            operatorManagementService.operatorDisconnected(user.getName());
        }

    }
}
