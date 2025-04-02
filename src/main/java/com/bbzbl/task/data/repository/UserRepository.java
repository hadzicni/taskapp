package com.bbzbl.task.data.repository;

import com.bbzbl.task.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 * Provides CRUD operations and custom queries.
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email address.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @return an Optional containing the deleted user if deletion was successful
     */
    Optional<User> deleteUserById(Long id);
}
