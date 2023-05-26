@healthCheck @registryApp
Feature: Registry App Checks

  Scenario Outline: Registry App Checks Return as Expected

    Given a registry service has started
    When a registry app check is requested for service "<ServiceName>"
    Then the registry app check returns up

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
