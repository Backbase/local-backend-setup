@smokeTest @user
Feature: User Feature

  Scenario: User Feature - Get Users

    Given a user has authenticated
    When the User Makes A Get Users Request
    Then the Response Returns 200

  Scenario: User Feature - Create and Delete User

    Given a user has authenticated
    Given the User Makes A Create Users Request
    When the User Makes A Delete Users Request
    Then the Response Returns 204
