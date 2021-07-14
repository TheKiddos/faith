Feature: Close Project
  Scenario: Close Project Successfully
    Given Close Dashboard Stakeholder logins
    And Close Dashboard Stakeholder goes to an open project dashboard
    When Close Dashboard Stakeholder clicks the close project button for a project
    Then Project Status Is Changed To Closed
