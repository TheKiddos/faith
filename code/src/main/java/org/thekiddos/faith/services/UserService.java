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

    /**
     * Activate the user account with the provided nickname and send an email to that user
     * if the user is already activated nothing should happen
     * if the user does not exists the method will simply log that and ignore it.
     * @param nickname The nickname if the user to activate
     */
    void activateUser( String nickname );
}
