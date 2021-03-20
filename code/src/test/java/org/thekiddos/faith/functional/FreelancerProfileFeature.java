package org.thekiddos.faith.functional;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.thekiddos.faith.Utils;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Skill;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.services.UserService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FreelancerProfileFeature {
    private final WebDriver webDriver;
    private final UserService userService;
    private final User user;

    @Autowired
    public FreelancerProfileFeature( WebDriver webDriver, UserService userService ) {
        this.webDriver = webDriver;
        this.userService = userService;
        this.user = Utils.getOrCreateTestUser( this.userService, "freelancer@test.com", "Freelancer" );
    }

    @Given( "Freelancer logins" )
    public void freelancerLogins() {
        webDriver.get( Utils.LOGIN_PAGE );
        webDriver.findElement( By.id( "email" ) ).sendKeys( "freelancer@test.com" );
        webDriver.findElement( By.id( "password" ) ).sendKeys( "password" );
        webDriver.findElement( By.id( "submit" ) ).click();
    }

    @And( "Freelancer visits my profile page" )
    public void freelancerVisitsMyProfilePage() {
        webDriver.get( Utils.FREELANCER_PROFILE );
    }

    @And( "Freelancer fills his details" )
    public void freelancerFillsHisDetails() {
        webDriver.findElement( By.id( "summary" ) ).sendKeys( "Blah Blah" );
        webDriver.findElement( By.id( "skills" ) ).sendKeys( "python\tc++ sleeping" );
        webDriver.findElement( By.id( "is-available" ) ).click();
    }

    @When( "Freelancer clicks the update button" )
    public void freelancerClicksTheUpdateButton() {
        webDriver.findElement( By.id( "submit-profile" ) ).click();
    }

    @Then( "Freelancer profile is updated" )
    public void freelancerProfileIsUpdated() {
        var user = (User) userService.loadUserByUsername( this.user.getEmail() );

        Freelancer freelancer = (Freelancer) user.getType();
        assertEquals( "Blah Blah", freelancer.getSummary() );
        assertTrue( freelancer.isAvailable() );

        Set<Skill> skills = Set.of( Skill.of( "python" ), Skill.of( "c++" ), Skill.of( "sleep" ) );
        assertEquals( skills, freelancer.getSkills() );
    }
}
