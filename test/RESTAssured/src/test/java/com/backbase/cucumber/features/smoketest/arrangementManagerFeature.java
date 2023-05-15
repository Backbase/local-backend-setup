package com.backbase.cucumber.features.smoketest;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {"pretty", "json:target/cucumber-reports/arrangementManager-report.json", "html:target/cucumber",
        "junit:target/junit-report.xml"},
    features = "src/test/resources/features/smoketest/arrangementManager.feature",
    glue = {"com/backbase/cucumber/steps/smoketest"}
)
public class arrangementManagerFeature {

}
