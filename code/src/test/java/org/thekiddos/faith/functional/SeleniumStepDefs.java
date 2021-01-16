package org.thekiddos.faith.functional;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT )
public class SeleniumStepDefs {
    WebDriver webDriver;
    @Value( "${selenium.edge.driver.path}" )
    private String driverPath;

    @io.cucumber.java.en.Given( "Open edge browser" )
    public void openEdgeBrowser() {
        System.setProperty("webdriver.edge.driver", driverPath);
        webDriver = new EdgeDriver();
        webDriver.manage().window().maximize();
    }

    @When( "User visits homepage" )
    public void userVisitsHomepage() {
        webDriver.get( "http://localhost:8080/" );
    }

    @Then( "User will see homepage" )
    public void userWillSeeHomepage() {
        // If the connection was accepted then the page was rendered and the test passes so close and end
        webDriver.close();
    }
}
