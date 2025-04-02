package com.bbzbl.task.config;

import com.bbzbl.task.data.entity.User;
import com.bbzbl.task.data.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initializes an administrator account at application startup
 * if it does not already exist.
 */
@Component
public class AdminInitializer implements CommandLineRunner {

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_EMAIL = "admin@example.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "password123!";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor for dependency injection.
     *
     * @param userRepository Repository for accessing user data
     * @param passwordEncoder Encoder for hashing passwords
     */
    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * This method is executed when the application starts.
     * It creates a default admin user if none exists.
     *
     * @param args Application startup arguments
     */
    @Override
    public void run(String... args) {
        if (adminAlreadyExists()) {
            logAdminAlreadyExists();
            return;
        }

        User adminUser = createAdminUser();
        userRepository.save(adminUser);
        logAdminCreated();
    }

    /**
     * Checks if the default admin user already exists in the database.
     *
     * @return true if the admin user exists, false otherwise
     */
    private boolean adminAlreadyExists() {
        return userRepository.findByUsername(DEFAULT_ADMIN_USERNAME).isPresent();
    }

    /**
     * Creates a new admin user with default credentials.
     *
     * @return a User object representing the admin user
     */
    private User createAdminUser() {
        return new User(
                DEFAULT_ADMIN_USERNAME,
                passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD),
                "Admin",
                "User",
                DEFAULT_ADMIN_EMAIL
        );
    }

    /**
     * Logs the creation of the admin user to the console.
     */
    private void logAdminCreated() {
        System.out.println("✅ Admin account created:");
        System.out.println("   Username: " + DEFAULT_ADMIN_USERNAME);
        System.out.println("   Password: " + DEFAULT_ADMIN_PASSWORD);
        System.out.println("   Email: " + DEFAULT_ADMIN_EMAIL);
    }

    /**
     * Logs a message indicating that the admin user already exists.
     */
    private void logAdminAlreadyExists() {
        System.out.println("⚠️ Admin account already exists.");
        System.out.println("   Username: " + DEFAULT_ADMIN_USERNAME);
        System.out.println("   (Default password would be: " + DEFAULT_ADMIN_PASSWORD + ")");
    }
}