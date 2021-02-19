package org.thekiddos.faith.functional;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.thekiddos.faith.Utils;
import org.thekiddos.faith.models.Email;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.services.EmailService;
import org.thekiddos.faith.services.UserService;
import org.thekiddos.faith.utils.EmailSubjectConstants;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class UserRegistrationTest {
    private final WebDriver webDriver;
    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public UserRegistrationTest( WebDriver webDriver, UserService userService, EmailService emailService ) {
        this.webDriver = webDriver;
        this.userService = userService;
        this.emailService = emailService;
    }

    @io.cucumber.java.en.Given( "A new user visits registration page" )
    public void userIsNotLoggedIn() {
        webDriver.manage().window().maximize();
        webDriver.get( Utils.SITE_ROOT + "register" );
        assertEquals( Utils.SITE_ROOT + "register", webDriver.getCurrentUrl() );
    }

    @io.cucumber.java.en.And( "User fills required info" )
    public void userFillsRequiredInfo() throws IOException {
        webDriver.findElement( By.id( "email" ) ).sendKeys( "testuser@test.com" );
        webDriver.findElement( By.id( "nickname" ) ).sendKeys( "tasty" );
        webDriver.findElement( By.id( "first-name" ) ).sendKeys( "Test" );
        webDriver.findElement( By.id( "last-name" ) ).sendKeys( "User" );
        webDriver.findElement( By.id( "password" ) ).sendKeys( "password" );
        webDriver.findElement( By.id( "password-confirm" ) ).sendKeys( "password" );
        String civilIdPath = new ClassPathResource("banner.txt").getFile().getAbsolutePath();
        webDriver.findElement( By.id( "civil-id" ) ).sendKeys( civilIdPath );
        webDriver.findElement( By.id( "phone-number" ) ).sendKeys( "+963987654321" );
        webDriver.findElement( By.id( "address" ) ).sendKeys( "+963987654321" );
        webDriver.findElement( By.id( "freelancer" ) ).click();
    }

    @io.cucumber.java.en.When( "User clicks submit button" )
    public void userClicksSubmitButton() {
        webDriver.findElement( By.id( "submit" ) ).click();
    }

    @io.cucumber.java.en.Then( "Account is created and deactivated" )
    public void accountIsCreatedAndDeactivated() {
        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS );
        User user = (User) userService.loadUserByUsername( "testuser@test.com" );
        assertFalse( user.isEnabled() );
        assertFalse( user.isAdmin() );
        assertEquals( Collections.singletonList( new SimpleGrantedAuthority( "USER" ) ), user.getAuthorities() );
    }

    @io.cucumber.java.en.And( "Admin receives an email" )
    public void adminReceivesAnEmail() {
        List<Email> emails = emailService.getEmails();
        assertEquals( 1, emails.size() );
        assertEquals( EmailSubjectConstants.USER_REQUIRES_APPROVAL, emails.get( 0 ).getSubject() );

        String adminEmail = emails.get( 0 ).getTo();
        User user = (User) userService.loadUserByUsername( adminEmail );
        assertTrue( user.isAdmin() );
    }

    @And( "User is redirected to thank you page" )
    public void userIsRedirectedToThankYouPage() {
        assertEquals( Utils.REGISTRATION_SUCCESSFUL_URL, webDriver.getCurrentUrl() );
        webDriver.close();
    }

    @Given( "User visits login page" )
    public void userVisitsLoginPage() {
        webDriver.get( Utils.LOGIN_PAGE );
    }

    @And( "User enters default admin credentials" )
    public void userEntersDefaultAdminCredentials() {
        webDriver.findElement( By.id( "email" ) ).sendKeys( "admin@faith.com" );
        webDriver.findElement( By.id( "password" ) ).sendKeys( "Admin@Fa1ith" );
    }

    @When( "User Clicks the login button" )
    public void userClicksTheLoginButton() {
        webDriver.findElement( By.id( "submit" ) ).click();
    }

    @Then( "User is redirected to admin panel" )
    public void userIsRedirectedToAdminPanel() {
        assertEquals( Utils.ADMIN_PANEL, webDriver.getCurrentUrl() );
        webDriver.close();
    }

    @Given( "Admin visits users admin page" )
    public void adminVisitsUsersAdminPage() {
        User user = (User) userService.loadUserByUsername( "testuser@test.com" ); // TODO: this uses the previous test is this OK?

        // Sanity Check
        assertFalse( user.isEnabled() );
        List<Email> emails = emailService.getEmailsFor( user.getEmail() );
        assertEquals( 0, emails.size() );

        webDriver.get( Utils.USER_ADMIN_PANEL );
    }

    @When( "Admin clicks the activate button on a deactivated user" )
    public void adminClicksTheActivateButtonOnADeactivatedUser() {
        webDriver.findElement( By.className( "enable-user-btn" ) ).click();
    }

    @Then( "User is activated" )
    public void userIsActivated() {
        User user = (User) userService.loadUserByUsername( "testuser@test.com" );
        assertTrue( user.isEnabled() );
    }

    @And( "User receives an email" )
    public void userReceivesAnEmail() {
        User user = (User) userService.loadUserByUsername( "testuser@test.com" );

        List<Email> emails = emailService.getEmailsFor( user.getEmail() );
        assertEquals( 1, emails.size() );
        assertEquals( EmailSubjectConstants.ACCOUNT_ACTIVATED, emails.get( 0 ).getSubject() );
    }
}
