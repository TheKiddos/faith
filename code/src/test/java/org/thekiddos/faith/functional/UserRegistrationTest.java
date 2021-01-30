package org.thekiddos.faith.functional;

import io.cucumber.java.en.And;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.thekiddos.faith.Utils;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.services.UserService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class UserRegistrationTest {
    private final WebDriver webDriver;
    private final UserService userService;

    @Autowired
    public UserRegistrationTest( WebDriver webDriver, UserService userService ) {
        this.webDriver = webDriver;
        this.userService = userService;
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
    }

    @io.cucumber.java.en.And( "Admin receives an email" )
    public void adminReceivesAnEmail() {
        // TODO
    }

    @And( "User is redirected to thank you page" )
    public void userIsRedirectedToThankYouPage() {
        assertEquals( Utils.REGISTRATION_SUCCESSFUL_URL, webDriver.getCurrentUrl() );
        webDriver.close();
    }
}
