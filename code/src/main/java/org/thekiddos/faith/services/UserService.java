package org.thekiddos.faith.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.exceptions.UserAlreadyExistException;
import org.thekiddos.faith.models.User;

import java.util.List;

public interface UserService extends UserDetailsService {
    /**
     * Creates and saves a user in the database
     * @param userDto The user data transfer object to create the user from
     * @return The created User
     * @throws UserAlreadyExistException If a user with the same email already exists
     */
    User createUser( UserDto userDto ) throws UserAlreadyExistException;

    /**
     * @return All users in database
     */
    List<User> getAll();

    /**
     * Disable the user and send an email for all admins to asking them to approve the user
     * @param user The user that requires approval
     */
    void requireAdminApprovalFor( User user );
}
