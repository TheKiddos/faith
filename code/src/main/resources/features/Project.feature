Feature: Create Project
  Scenario: Public Project
    Given Stakeholder logins
    And Stakeholder visits my projects page
    And Stakeholder clicks add project buttons
    And Stakeholder fills project details
    And Stakeholder sets the project public
    When Stakeholder clicks the submit button
    Then Project is added
    And Project is seen on homepage new projects section

  Scenario: Private Project
    Given Stakeholder logins
    And Stakeholder visits my projects page
    And Stakeholder clicks add project buttons
    And Stakeholder fills project details
    And Stakeholder sets the project private
    When Stakeholder clicks the submit button
    Then Project is added
    And Project is seen on homepage new projects section
