package com.bbzbl.task.security;

import com.bbzbl.task.data.entity.User;
import com.bbzbl.task.data.repository.UserRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Utility component to access the currently authenticated {@link User}
 * and handle logout functionality.
 */
@Component
public class AuthenticatedUser {

    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    /**
     * Constructor for injecting dependencies.
     *
     * @param authenticationContext the current authentication context
     * @param userRepository        the repository to fetch user entities
     */
    public AuthenticatedUser(AuthenticationContext authenticationContext, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Retrieves the currently authenticated {@link User}, if available.
     *
     * @return an Optional containing the current user or empty if not authenticated
     */
    @Transactional
    public Optional<User> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .flatMap(userDetails -> userRepository.findByUsername(userDetails.getUsername()));
    }

    /**
     * Logs out the currently authenticated user.
     */
    public void logout() {
        authenticationContext.logout();
    }
}
