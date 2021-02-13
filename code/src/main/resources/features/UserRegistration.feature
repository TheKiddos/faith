Feature: User Registration

  Scenario: Successful Request Submission
    Given A new user visits registration page
    And User fills required info
    When User clicks submit button
    Then Account is created and deactivated
    And Admin receives an email
    And User is redirected to thank you page


  Scenario: Default Admin Account
    Given User visits login page
    And User enters default admin credentials
    When User Clicks the login button
    Then User is redirected to admin panel


  Scenario: Admin Accepts Request
    Given Admin visits a deactivated user management page
    When Admin clicks the activate button
    Then User is activated
    And User receives an email
