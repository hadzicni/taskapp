package com.bbzbl.task.services;

import com.bbzbl.task.data.entity.User;
import com.bbzbl.task.data.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for managing user-related actions such as registration,
 * updating user info, password encoding, and deletion.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    /**
     * Constructor for injecting dependencies.
     *
     * @param passwordEncoder the password encoder for hashing passwords
     * @param repository      the repository for user data access
     */
    public UserService(PasswordEncoder passwordEncoder, UserRepository repository) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the user's ID
     * @return an Optional containing the user, if found
     */
    public Optional<User> get(Long id) {
        return repository.findById(id);
    }

    /**
     * Registers a new user if username, email, and password are valid.
     *
     * @return true if registration succeeds, false if username or email already exist
     */
    @Transactional
    public boolean registerUser(String username, String password, String firstName, String lastName, String email) {
        if (repository.findByUsername(username).isPresent()) {
            logger.warn("Registration failed: Username '{}' already exists.", username);
            return false;
        }

        if (repository.findByEmail(email).isPresent()) {
            logger.warn("Registration failed: Email '{}' already exists.", email);
            return false;
        }

        if (!isValidPassword(password)) {
            logger.warn("Registration failed: Password does not meet security requirements.");
            throw new IllegalArgumentException("Password must be at least 8 characters and contain a number and a special character.");
        }

        User user = new User(username, passwordEncoder.encode(password), firstName, lastName, email);
        repository.save(user);
        logger.info("User '{}' successfully registered.", username);
        return true;
    }

    /**
     * Retrieves a user by username.
     *
     * @param username the username to search
     * @return an Optional containing the user if found
     */
    public Optional<User> getUserByUsername(String username) {
        return repository.findByUsername(username);
    }

    /**
     * Updates an existing user.
     *
     * @param user the user to update
     * @return an Optional with the updated user or empty if user not found
     */
    public Optional<User> updateUser(User user) {
        if (repository.existsById(user.getId())) {
            repository.save(user);
            logger.info("User '{}' successfully updated.", user.getUsername());
            return Optional.of(user);
        } else {
            logger.warn("Update failed: User '{}' not found.", user.getUsername());
            return Optional.empty();
        }
    }

    /**
     * Deletes a user by ID.
     *
     * @param user the user to delete
     */
    @Transactional
    public void deleteUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Cannot delete user: Invalid user data.");
        }

        try {
            repository.deleteUserById(user.getId());
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalStateException("User could not be deleted because they do not exist.", e);
        }
    }

    /**
     * Validates if the password meets complexity requirements.
     *
     * @param password the password to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$");
    }

    /**
     * Encodes the password using the configured encoder.
     *
     * @param password raw password
     * @return hashed password
     */
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Persists the user to the database.
     *
     * @param user the user to save
     */
    @Transactional
    public void saveUser(User user) {
        repository.save(user);
    }
}
