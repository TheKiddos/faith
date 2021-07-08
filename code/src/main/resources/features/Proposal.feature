Feature: Proposal Feature
  Scenario: Stakeholder Sends Proposal
    Given Proposal Stakeholder logs in
    And stakeholder goes to a project dashboard
    And stakeholder sets his proposal amount to a freelancer
    When stakeholder clicks the send proposal button
    Then proposal is saved
    And freelancer is informed by email that a new proposal was sent

  Scenario: Freelancer Accepts Proposal
    Given Proposal Freelancer logs in
    And freelancer visits my proposals page
    When Freelancer clicks the accept button
    Then proposal status is changed to accepted
    And stakeholder is informed by email that proposal was accepted

  # TODO: Stakeholder checks dashboard of a project that has a freelancer

  Scenario: Freelancer Rejects Proposal
    Given Proposal Freelancer logs in
    And freelancer visits my proposals page
    When Freelancer clicks the accept button
    Then proposal status is changed to rejected
    And stakeholder is informed by email that proposal was rejected
