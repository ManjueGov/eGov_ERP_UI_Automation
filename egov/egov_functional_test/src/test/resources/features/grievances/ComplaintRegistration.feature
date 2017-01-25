Feature: Register Complaint

  As a citizen register complaint directly in website

  @Sanity

  Scenario Outline: Register a Complaint with Citizen Login

    Given citizen logs in
    When he choose to register complaint with his login
    And he choose to enter grievance details as <grievanceDetails>
    And he copies CRN and closes the acknowledgement
    And current user sign out

    When creator logs in
    And choose to act upon the above CRN
    And he resolves the issue and mark status as completed
#    And current user logs out


    Examples:
    |grievanceDetails|
    |grievanceDetails|

  @WIP

  Scenario Outline: Register Complaint anonymously

    Given user log on to the website
    When he choose to register a complaint
    And he choose to enter contact information as <contactDetails>
    And he choose to enter grievance details as <grievanceDetails>

    Examples:
      |contactDetails |grievanceDetails |
      |contactInfo    |grievanceDetails |


  @WIP

  Scenario: