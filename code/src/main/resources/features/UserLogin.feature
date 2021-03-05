Feature: User Login

  Scenario: Forgot Password
    Given a user visits forgot password page
    And user enters his email
    When user clicks the submit button
    Then a unique token is generated
    And an email with instruction on how to reset the password is sent to the user

  Scenario: Password Reset
    Given a user with a token visits the password reset page
    And user enters his new password and password confirm
    When user clicks the submit button
    Then his password changes
    And token is deleted
