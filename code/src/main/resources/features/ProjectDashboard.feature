Feature: Project Dashboard
  Scenario: Open Project Dashboard Successfully
    Given Dashboard Stakeholder logins
    And Dashboard Stakeholder visits my projects page
    When Stakeholder clicks dashboard button for a project
    Then Stakeholder is redirected to project dashboard page
