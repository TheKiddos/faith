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
import org.thekiddos.faith.repositories.BidCommentRepository;
import org.thekiddos.faith.repositories.BidRepository;
import org.thekiddos.faith.repositories.ProjectRepository;
import org.thekiddos.faith.services.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectFeature {
    private final UserService userService;
    private final WebDriver webDriver;
    private final ProjectRepository projectRepository;
    private final BidCommentRepository bidCommentRepository;
    private final BidRepository bidRepository;
    private final User user;
    
    @Autowired
    public ProjectFeature( UserService userService, WebDriver webDriver, ProjectRepository projectRepository, BidCommentRepository bidCommentRepository, BidRepository bidRepository ) {
        this.userService = userService;
        this.webDriver = webDriver;
        this.projectRepository = projectRepository;
        this.user = Utils.getOrCreateTestUser( userService, "stakeholder@test.com", "Stakeholder" );
        this.bidCommentRepository = bidCommentRepository;
        this.bidRepository = bidRepository;

        setup();
    }

    private void setup() {
        bidCommentRepository.deleteAll();
        bidRepository.deleteAll();
        projectRepository.deleteAll();
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
            webDriver.findElement( By.id( "preferred-bid" ) ).clear();
            webDriver.findElement( By.id( "preferred-bid" ) ).sendKeys( "200.0" );
            webDriver.findElement( By.id( "duration" ) ).clear();
            webDriver.findElement( By.id( "duration" ) ).sendKeys( "31" );
            webDriver.findElement( By.id( "minimum-qualification" ) ).clear();
            webDriver.findElement( By.id( "minimum-qualification" ) ).sendKeys( "100" );
    }

    @And( "Stakeholder sets the project public" )
    public void stakeholderSetsTheProjectPublic() {
        webDriver.findElement( By.id( "allow-bidding" ) ).click();
    }

    @When( "Stakeholder clicks the submit button" )
    public void stakeholderClicksTheSubmitButton() {
        webDriver.findElement( By.id( "submit-project" ) ).click();
    }

    @Then( "Project is added with {string} for bidding" )
    public void projectIsAddedWithForBidding( String bidding ) {
        var projects = projectRepository.findAll();

        assertFalse( projects.isEmpty() );

        var project = projects.get( 0 );
        assertNotNull( project );
        assertEquals( "new world order", project.getName() );
        assertEquals( "Make all people slaves", project.getDescription() );
        assertEquals( 200.0, project.getPreferredBid() );
        assertEquals( 31, project.getDuration().toDays() );
        assertEquals( 100, project.getMinimumQualification() );
        assertEquals( bidding, String.valueOf( project.isAllowBidding() ) );
        assertNotNull( project.getOwner() );
        assertEquals( this.user.getEmail(), project.getOwner().getUser().getEmail() );

        projectRepository.deleteAll(); // For other tests
        webDriver.close();
    }

    @And( "Stakeholder sets the project private" )
    public void stakeholderSetsTheProjectPrivate() {
        // Default value is private so no need to do anything here right?
    }
}
