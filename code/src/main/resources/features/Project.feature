Feature: Create Project
  Scenario: Public Project
    Given Stakeholder logins
    And Stakeholder visits my projects page
    And Stakeholder clicks add project buttons
    And Stakeholder fills project details
    And Stakeholder sets the project public
    When Stakeholder clicks the submit button
    Then Project is added with "true" for bidding

  Scenario: Private Project
    Given Stakeholder logins
    And Stakeholder visits my projects page
    And Stakeholder clicks add project buttons
    And Stakeholder fills project details
    And Stakeholder sets the project private
    When Stakeholder clicks the submit button
    Then Project is added with "false" for bidding
