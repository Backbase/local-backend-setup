@smokeTest @accessControl
Feature: Access Control

  Scenario: Access Control - Get Data Groups

    Given a user has authenitcated for access control
    When the User Requests To Get Data Groups
    Then the Response Returns OK

  Scenario: Access Control - Get User Context Service Agreements

    Given a user has authenitcated for access control
    When the User Requests To Get User Context Service Agreements
    Then the Response Returns OK

  Scenario: Access Control - Set User Context Service Agreements

    Given a user has authenitcated for access control
    When the User Requests To Set User Context Service Agreements
    Then the Response Returns No Content Success

  Scenario: Access Control - Get Function Groups

    Given a user has authenitcated for access control
    When the User Requests To Get Function Groups
    Then the Response Returns OK

  Scenario: Access Control - Get User Permissions Summary

    Given a user has authenitcated for access control
    When the User Requests To Get User Permissions Summary
    Then the Response Returns OK
