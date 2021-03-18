package org.thekiddos.faith.functional;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.thekiddos.faith.Utils;
import org.thekiddos.faith.services.UserService;
import org.thekiddos.faith.repositories.*;
import org.thekiddos.faith.models.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectFeature {
    private final UserService userService;
    private final WebDriver webDriver;
    private final ProjectRepository projectRepository;
    private final User user;
    
    @Autowired
    public ProjectFeature( UserService userService, WebDriver webDriver, ProjectRepository projectRepository ) {
        this.userService = userService;
        this.webDriver = webDriver;
        this.projectRepository = projectRepository;
        this.user = Utils.getOrCreateTestUser( userService, "stakeholder@test.com", "Stakeholder" );
    }

    @Given( "Stakeholder logins" )
    public void stakeholderLogins() {
        webDriver.get( Utils.LOGIN_PAGE );
        webDriver.findElement( By.id( "email" ) ).sendKeys( "stakeholder@test.com" );
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
            webDriver.findElement( By.id( "name" ) ).sendKeys( "new world order" );
            webDriver.findElement( By.id( "description" ) ).sendKeys( "Make all people slaves" );
            webDriver.findElement( By.id( "preferredBid" ) ).sendKeys( "200.0" );
            webDriver.findElement( By.id( "duration" ) ).sendKeys( "31" );
            webDriver.findElement( By.id( "minimumQualification" ) ).sendKeys( "100" );
    }

    @And( "Stakeholder sets the project public" )
    public void stakeholderSetsTheProjectPublic() {
        webDriver.findElement( By.id( "allowBidding" ) ).click();
    }

    @When( "Stakeholder clicks the submit button" )
    public void stakeholderClicksTheSubmitButton() {
        webDriver.findElement( By.id( "submit" ) ).click();
    }

    @Then( "Project is added" )
    public void projectIsAdded() {
        var projects = projectRepository.findAll();
        
        assertFalse( projects.isEmpty() );
        
        var project = projects.get( 0 );
        assertNotNull( project );
        assertEquals( "new world order", project.getName() );
        assertEquals( "Make all people slaves", project.getDescription() );
        assertEquals( 200.0, project.getPreferredBid() );
        assertEquals( 31, project.getDuration() );
        assertEquals( 100, project.getMinimumQualification() );
        assertEquals( true, project.isAllowBidding() );
        assertEquals( (Stakeholder) user.getType(), project.getOwner() );
    }

    @And( "Project is seen on homepage new projects section" )
    public void projectIsSeenOnHomepageNewProjectsSection() {
        var project = projectRepository.findAll().get( 0 );
        webDriver.findElement( By.id( "project_" + project.getId() ) );
    }

    @And( "Stakeholder sets the project private" )
    public void stakeholderSetsTheProjectPrivate() {
        // Default value is private so no need to do anything here right?
    }
}
