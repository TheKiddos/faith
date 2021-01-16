package org.thekiddos.faith.functional;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith( Cucumber.class )
@CucumberOptions(plugin = {"pretty"}, features = "classpath:features")
public class CucumberTestsRunner {
}
