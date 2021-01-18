package org.thekiddos.faith.functional;

import io.cucumber.java.en.And;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class UserRegistrationTest {
    private final WebDriver webDriver;

    @Autowired
    public UserRegistrationTest( WebDriver webDriver ) {
        this.webDriver = webDriver;
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
    }

    @io.cucumber.java.en.And( "Admin receives an email" )
    public void adminReceivesAnEmail() {

    }

    @And( "User is redirected to thank you page" )
    public void userIsRedirectedToThankYouPage() {
        webDriver.close();
    }
}
