@smokeTest @arrangementManager
Feature: Arrangement Manager

  Scenario: Arrangement Manager - Get Balances Aggregations

    Given a user has authenticated for arrangement manager
    When the User Requests To Get Balances Aggregations
    Then the Response Returns As Expected

  Scenario: Arrangement Manager - Get Product Kinds

    Given a user has authenticated for arrangement manager
    When the User Requests To Get Product Kinds
    Then the Response Returns As Expected
