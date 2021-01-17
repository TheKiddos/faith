package org.thekiddos.faith.functional;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Sanity test to verify selenium and browser are working
 */
@CucumberContextConfiguration
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT )
public class SeleniumStepDefs {
    private final WebDriver webDriver;

    @Autowired
    public SeleniumStepDefs( WebDriver webDriver ) {
        this.webDriver = webDriver;
    }

    @io.cucumber.java.en.Given( "Open edge browser" )
    public void openEdgeBrowser() {
        webDriver.manage().window().maximize();
    }

    @When( "User visits homepage" )
    public void userVisitsHomepage() {
        webDriver.get( Utils.SITE_ROOT );
    }

    @Then( "User will see homepage" )
    public void userWillSeeHomepage() {
        // If the connection was accepted then the page was rendered and the test passes so close and end
        webDriver.close();
    }
}
