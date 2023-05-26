package com.backbase.cucumber.features.healthcheck;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {"pretty", "json:target/cucumber-reports/registryAppCheck-report.json", "html:target/cucumber",
    "junit:target/junit-report.xml"},
    features = "src/test/resources/features/healthcheck/registryApp.feature",
    glue = {"com/backbase/cucumber/steps/healthcheck"}
)
public class registryAppFeature {
}
