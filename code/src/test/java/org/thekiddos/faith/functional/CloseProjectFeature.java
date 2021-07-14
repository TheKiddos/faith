package org.thekiddos.faith.functional;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.thekiddos.faith.Utils;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.models.Stakeholder;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.ProjectRepository;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.UserService;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CloseProjectFeature {
    private final WebDriver webDriver;
    private final UserService userService;
    private final ProjectService projectService;

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private User stakeholder;
    private Project project;

    @Autowired
    public CloseProjectFeature( WebDriver webDriver, UserService userService, ProjectService projectService, ProjectRepository projectRepository, UserRepository userRepository ) {
        this.webDriver = webDriver;
        this.userService = userService;
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        setUp();
    }

    private void setUp() {
        this.stakeholder = Utils.getOrCreateTestUser( userService, "stakeholder@bidding.com", "Stakeholder" );
        var projectDto = ProjectDto.builder()
                .allowBidding( true )
                .description( "Hellooooo" )
                .duration( 3 )
                .minimumQualification( 20 )
                .name( "Death" )
                .preferredBid( 200 )
                .build();

        this.project = projectService.createProjectFor( (Stakeholder) stakeholder.getType(), projectDto );
    }

    @Given( "Close Dashboard Stakeholder logins" )
    public void closeDashboardStakeholderLogins() {
        webDriver.get( Utils.LOGIN_PAGE );
        webDriver.manage().window().maximize();

        webDriver.findElement( By.id( "email" ) ).sendKeys( stakeholder.getEmail() );
        webDriver.findElement( By.id( "password" ) ).sendKeys( "password" );
        webDriver.findElement( By.id( "submit" ) ).click();
    }

    @And( "Close Dashboard Stakeholder goes to an open project dashboard" )
    public void closeDashboardStakeholderGoesToAnOpenProjectDashboard() {
        webDriver.get( Utils.getProjectDashboardPage( project.getId() ) );
    }

    @When( "Close Dashboard Stakeholder clicks the close project button for a project" )
    public void closeDashboardStakeholderClicksTheCloseProjectButtonForAProject() {
        webDriver.findElement( By.id( "close-project-btn" ) ).click();
    }

    @Then( "Project Status Is Changed To Closed" )
    public void closeDashboardStakeholderIsRedirectedToProjectDashboardPage() {
        assertTrue( projectRepository.findById( project.getId() ).orElse( new Project() ).isClosed() );
    }
}
