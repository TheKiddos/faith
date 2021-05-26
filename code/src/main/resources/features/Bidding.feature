Feature: Bid on Project
  Scenario: Successful Bid
    Given Bidding Freelancer logins
    And user visits a public project page
    And Freelancer adds his bid
    When Freelancer clicks the Bid button
    Then Bid is added to project
    And Project's owner is notified by email
    And Bidding Close Browser

  Scenario: Successful Bid With Comment
    Given Bidding Freelancer logins
    And user visits a public project page
    And Freelancer adds his bid
    And Freelancer adds a comment
    When Freelancer clicks the Bid button
    Then Bid is added to project
    And Comment is added
    And Project's owner is notified by email
    And Bidding Close Browser

  Scenario: Stakeholder Comment on Bidding
    Given Bidding Stakeholder logins
    And user visits a public project page
    And stakeholder types a comment on a bidding
    When stakeholder clicks add comment button
    Then comment is saved
    And Bidding Close Browser

# TODO: Freelancer only comment on his bidding plus the following test:
#  Scenario: Freelancer Comment on Bidding
#    Given Freelancer logins
#    And user visits a public project page
#    And freelancer types a comment on his bidding
#    When freelancer clicks add comment button
#    Then comment is saved

# TODO: Minimum Qualification
