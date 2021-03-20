Feature: Edit Profile
  Scenario: Successful Update
    Given Freelancer logins
    And Freelancer visits my profile page
    And Freelancer fills his details
    When Freelancer clicks the update button
    Then Freelancer profile is updated
