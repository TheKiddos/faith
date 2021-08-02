package org.thekiddos.faith.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.exceptions.UserAlreadyExistException;
import org.thekiddos.faith.models.PasswordResetToken;
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

    /**
     * Delete the user account with the provided nickname and send an email to that user
     * if the user does not exists the method will simply log that and ignore it.
     * if the user was accepted this method will throw a RuntimeException
     *
     * @param nickname The nickname if the user to delete
     * @throws RuntimeException if the user was accepted before
     */
    void rejectUser( String nickname );

    /**
     * Creates and send a unique token to user email so he can reset his password
     * if the user doesn't exists this method will log and return null
     *
     * @return The token generated or null if no user with the specified email was found
     */
    PasswordResetToken createForgotPasswordToken( String email );

    /**
     * Changes the specified user password if the user has a reset password token
     * @param token The password reset token value
     * @param newPassword The new password to use
     */
    void resetUserPassword( String token, String newPassword );

    User findByEmail( String email ) throws UsernameNotFoundException;
}
