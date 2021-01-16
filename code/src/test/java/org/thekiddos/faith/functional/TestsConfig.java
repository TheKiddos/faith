package org.thekiddos.faith.functional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class TestsConfig {
    @Value( "${selenium.edge.driver.path}" )
    private String driverPath;

    /**
     * Method used for selenium test framework only
     * @return Webdriver for edge browser
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public WebDriver edgeDriver() {
        System.setProperty("webdriver.edge.driver", driverPath);
        return new EdgeDriver();
    }
}
