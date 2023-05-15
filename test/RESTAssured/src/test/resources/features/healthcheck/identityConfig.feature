@healthCheck @identityConfig
Feature: Identity Config

  Scenario: Identity Config Checks Returns as Expected

    Given an Identity service has started
    When an Identity config check is requested
    Then the Identity config check returns as expected
