package com.backbase.cucumber;

import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

@PropertySource("application.properties")
@ComponentScan("com.backbase.cucumber.config")
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
public class Config {

}
