@healthCheck @actuator
Feature: Actuator HeathChecks

  Scenario Outline: HeathChecks Return as Expected

    Given a service has started for "<ServiceName>"
    When a healthcheck is requested for service "<ServiceName>"
    Then the heathcheck returns up for service "<ServiceName>"

    @accessControl
    Examples:
      | ServiceName    |
      | access-control |

    @arrangementManager
    Examples:
      | ServiceName         |
      | arrangement-manager |

    @tokenConverter
    Examples:
      | ServiceName     |
      | token-converter |

    @userManager
    Examples:
      | ServiceName  |
      | user-manager |
