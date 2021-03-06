package org.thekiddos.faith.functional;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.thekiddos.faith.Utils;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.EmailRepository;
import org.thekiddos.faith.repositories.PasswordResetTokenRepository;
import org.thekiddos.faith.services.UserService;
import org.thekiddos.faith.utils.EmailSubjectConstants;

import static org.junit.jupiter.api.Assertions.*;

public class UserLoginFeature {
    private final WebDriver webDriver;
    private final UserService userService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailRepository emailRepository;

    @Autowired
    public UserLoginFeature( WebDriver webDriver, UserService userService, PasswordResetTokenRepository passwordResetTokenRepository, EmailRepository emailRepository ) {
        this.webDriver = webDriver;
        this.userService = userService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailRepository = emailRepository;
    }

    @Given( "a user visits forgot password page" )
    public void aUserVisitsForgotPasswordPage() {
        webDriver.get( Utils.FORGOT_PASSWORD_URL );
    }

    @And( "user enters his email" )
    public void userEntersHisEmail() {
        User user = Utils.getOrCreateTestUser( userService );

        webDriver.findElement( By.id( "email" ) ).sendKeys( user.getEmail() );
    }

    @When( "user clicks the submit button" )
    public void userClicksTheSubmitButton() {
        webDriver.findElement( By.id( "submit" ) ).click();
    }

    @Then( "a unique token is generated" )
    public void aUniqueTokenIsGenerated() {
        User user = Utils.getOrCreateTestUser( userService );
        var tokenOptional = passwordResetTokenRepository.findByUser_Email( user.getEmail() );
        assertNotNull( user.getPasswordResetToken() );
        assertTrue( tokenOptional.isPresent() );
        assertEquals( user, tokenOptional.get().getUser() );
    }

    @And( "an email with instruction on how to reset the password is sent to the user" )
    public void anEmailWithInstructionOnHowToResetThePasswordIsSentToTheUser() {
        User user = Utils.getOrCreateTestUser( userService );

        var emails = emailRepository.findAll();
        var emailOptional = emails.stream().filter( email -> email.getSubject().equals( EmailSubjectConstants.PASSWORD_RESET ) ).findAny();

        assertTrue( emailOptional.isPresent() );
        assertEquals( user.getEmail(), emailOptional.get().getTo() );

        webDriver.close();
    }

    @Given( "a user with a token visits the password reset page" )
    public void aUserWithATokenVisitsThePasswordResetPage() {
        User user = Utils.getOrCreateTestUser( userService );
        var token = userService.createForgotPasswordToken( user.getEmail() );

        webDriver.get( Utils.getPasswordResetTokenUrl( token.getToken() ) );
    }

    @And( "user enters his new password and password confirm" )
    public void userEntersHisNewPasswordAndPasswordConfirm() {
        webDriver.findElement( By.id( "password" ) ).sendKeys( "newpassword" );
        webDriver.findElement( By.id( "password-confirm" ) ).sendKeys( "newpassword" );
    }

    // Click button is above

    @Then( "his password changes" )
    public void hisPasswordChanges() {
        User user = Utils.getOrCreateTestUser( userService );
        assertTrue( user.checkPassword( "newpassword" ) );
    }

    @And( "token is deleted" )
    public void tokenIsDeleted() {
        User user = Utils.getOrCreateTestUser( userService );
        assertNull( user.getPasswordResetToken() );
        assertTrue( passwordResetTokenRepository.findByUser_Email( user.getEmail() ).isEmpty() );
        webDriver.close();
    }
}
