package org.thekiddos.faith.functional;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

public class PublicProjectFeature {
    private final WebDriver webDriver;

    public PublicProjectFeature( WebDriver webDriver ) {
        this.webDriver = webDriver;
    }

    @Given( "a user open list of available projects for bidding" )
    public void aUserOpenListOfAvailableProjectsForBidding() {
    }

    @When( "user clicks on a project" )
    public void userClicksOnAProject() {
    }

    @Then( "user is redirected to project details page" )
    public void userIsRedirectedToProjectDetailsPage() {
    }
}
