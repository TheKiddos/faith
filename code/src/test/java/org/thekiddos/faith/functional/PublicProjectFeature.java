package org.thekiddos.faith.functional;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.thekiddos.faith.Utils;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.models.Stakeholder;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.UserService;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PublicProjectFeature {
    private final WebDriver webDriver;
    private final ProjectService projectService;
    private final UserService userService;

    @Autowired
    public PublicProjectFeature( WebDriver webDriver, ProjectService projectService, UserService userService ) {
        this.webDriver = webDriver;
        this.projectService = projectService;
        this.userService = userService;
        setUp();
    }

    void setUp() {
        User stakeholderUser = Utils.getOrCreateTestUser( userService, "stakeholder@projects.com", "Stakeholder" );
        var project = ProjectDto.builder()
                .allowBidding( true )
                .description( "Hellooooo" )
                .duration( 3 )
                .minimumQualification( 20 )
                .name( "Death" )
                .preferredBid( 200 )
                .build();
        projectService.createProjectFor( (Stakeholder) stakeholderUser.getType(), project );

        // TODO: create non public project and make sure it's not on page
//        project.setAllowBidding( false );
//        projectService.createProjectFor( (Stakeholder) stakeholderUser.getType(), project );
    }

    @Given( "a user open list of available projects for bidding" )
    public void aUserOpenListOfAvailableProjectsForBidding() {
        webDriver.get( Utils.PUBLIC_PROJECTS_PAGE );
    }

    @When( "user clicks on a project" )
    public void userClicksOnAProject() {
        webDriver.findElement( By.className( "btn" ) ).click();
    }

    @Then( "user is redirected to project details page" )
    public void userIsRedirectedToProjectDetailsPage() {
        assertTrue( isProjectDetailsUrl( webDriver.getCurrentUrl() ) );
        webDriver.close();
    }

    private boolean isProjectDetailsUrl( String currentUrl ) {
        return currentUrl.matches( Utils.PUBLIC_PROJECT_DETAILS_PAGE_PREFIX + "\\d+" );
    }
}
