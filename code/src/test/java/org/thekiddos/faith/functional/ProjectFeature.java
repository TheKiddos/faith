package org.thekiddos.faith.functional;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.thekiddos.faith.Utils;
import org.thekiddos.faith.services.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectFeature {
    private final UserService userService;
    private final WebDriver webDriver;

    public ProjectFeature( UserService userService, WebDriver webDriver ) {
        this.userService = userService;
        this.webDriver = webDriver;
    }

    @Given( "Stakeholder logins" )
    public void stakeholderLogins() {
        var email = "stakeholder@test.com";
        var type = "Stakeholder";

        Utils.getOrCreateTestUser( userService, email, type );

        webDriver.get( Utils.LOGIN_PAGE );
        webDriver.findElement( By.id( "email" ) ).sendKeys( email );
        webDriver.findElement( By.id( "password" ) ).sendKeys( "password" );
        webDriver.findElement( By.id( "submit" ) ).click();
    }

    @And( "Stakeholder visits my projects page" )
    public void stakeholderVisitsMyProjectsPage() {
        webDriver.get( Utils.MY_PROJECTS_PAGE );
    }

    @And( "Stakeholder clicks add project buttons" )
    public void stakeholderClicksAddProjectButtons() {
        webDriver.findElement( By.id( "add-project" ) ).click();
        assertEquals( Utils.ADD_PROJECT_PAGE , webDriver.getCurrentUrl() );
    }

    @And( "Stakeholder fills project details" )
    public void stakeholderFillsProjectDetails() {
    }

    @And( "Stakeholder sets the project public" )
    public void stakeholderSetsTheProjectPublic() {
    }

    @When( "Stakeholder clicks the submit button" )
    public void stakeholderClicksTheSubmitButton() {
    }

    @Then( "Project is added" )
    public void projectIsAdded() {
    }

    @And( "Project is seen on homepage new projects section" )
    public void projectIsSeenOnHomepageNewProjectsSection() {
    }

    @And( "Stakeholder sets the project private" )
    public void stakeholderSetsTheProjectPrivate() {
    }
}
