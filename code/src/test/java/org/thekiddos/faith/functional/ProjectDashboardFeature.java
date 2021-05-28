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
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectDashboardFeature {
    private final UserService userService;
    private final WebDriver webDriver;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final Project project;
    
    @Autowired
    public ProjectDashboardFeature( UserService userService, WebDriver webDriver, ProjectRepository projectRepository, ProjectService projectService ) {
        this.userService = userService;
        this.webDriver = webDriver;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        var user = Utils.getOrCreateTestUser( userService, "stakeholder@test.com", "Stakeholder" );
        project = findOrCreateProjectFor( user );
    }

    private Project findOrCreateProjectFor( User stakeholderUser ) {
        var stakeholder = (Stakeholder) stakeholderUser.getType();
        var projects = projectRepository.findByOwner( stakeholder );
        if ( !projects.isEmpty() )
            return projects.get( 0 );

        var projectDto = ProjectDto.builder().name( "Abh" ).description( "abh" ).minimumQualification( 200 ).preferredBid( 200 ).allowBidding( true ).duration( 2L ).build();
        return projectService.createProjectFor( stakeholder, projectDto );
    }

    @Given( "Dashboard Stakeholder logins" )
    public void stakeholderLogins() {
        webDriver.get( Utils.LOGIN_PAGE );
        webDriver.findElement( By.id( "email" ) ).sendKeys( "stakeholder@test.com" );
        webDriver.findElement( By.id( "password" ) ).sendKeys( "password" );
        webDriver.findElement( By.id( "submit" ) ).click();
    }

    @And( "Dashboard Stakeholder visits my projects page" )
    public void stakeholderVisitsMyProjectsPage() {
        webDriver.get( Utils.MY_PROJECTS_PAGE );
    }

    @When( "Stakeholder clicks dashboard button for a project" )
    public void stakeholderClicksDashboardButtonForAProject() {
        webDriver.findElement( By.id( "project-dashboard-btn-" + project.getId() ) ).click();
    }

    @Then( "Stakeholder is redirected to project dashboard page" )
    public void stakeholderIsRedirectedToProjectDashboardPage() {
        assertEquals( Utils.getProjectDashboardPage( project.getId() ) , webDriver.getCurrentUrl() );
    }
}
