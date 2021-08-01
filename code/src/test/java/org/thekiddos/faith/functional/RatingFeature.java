package org.thekiddos.faith.functional;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.thekiddos.faith.Utils;
import org.thekiddos.faith.services.UserService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class RatingFeature {
    private final WebDriver webDriver;
    private final UserService userService;

    @Autowired
    public RatingFeature( WebDriver webDriver, UserService userService ) {
        this.webDriver = webDriver;
        this.userService = userService;
        setUp();
    }

    private void setUp() {
        Utils.getOrCreateTestUser( userService, "freelancer@rating.com", "Freelancer" );
    }

    @When( "Rating User Opens Homepage" )
    public void ratingUserOpensHomepage() {
        webDriver.manage().window().maximize();
        webDriver.get( Utils.SITE_ROOT );
    }

    @Then( "He Can See Each Freelancer Rating" )
    public void heCanSeeEachFreelancerRating() {
        assertDoesNotThrow( () -> webDriver.findElement( By.className( "rating" ) ) );
        webDriver.close();
    }
}
