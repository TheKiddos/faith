Feature: User Registration

  Scenario: Successful Request Submission
    Given A new user visits registration page
    And User fills required info
    When User clicks submit button
    Then Account is created and deactivated
    And Admin receives an email
    And User is redirected to thank you page
