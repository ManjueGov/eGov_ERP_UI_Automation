Feature: Create New Property

  As a register user of the system
  I want to be able to create a new property
  So that the property records are up to date.


  @Sanity
  Scenario Outline: Registered user creating a new property in the system
    Given juniorAssistant logs in
    When he chooses to create new property
    And he enters property header details as <propertyHeaderDetails>
    And he enters owner details for the first owner as <ownerDetails>
    And he enters property address details as <propertyAddressDetails>
    And he enters assessment details as <assessmentDetails>


    And he enters amenities as <amenitiesDetails>
    And he enters construction type details as <constructionTypeDetails>
    And he enters floor details as <floorDetails>
    And he forwards for approval to billCollector
    Then property details get saved successfully

    And current user closes acknowledgement
    And current user logs out

    When billCollector logs in
    And chooses to act upon the above application
    And he forwards for approval to revenueInspector
    And current user closes acknowledgement
    And current user logs out

    When revenueInspector logs in
    And chooses to act upon the above application
    And he forwards for approval to revenueOfficer
    And current user closes acknowledgement
    And current user logs out

    When revenueOfficer logs in
    And chooses to act upon the above application
    And he forwards for approval to commissioner
    And current user closes acknowledgement
    And current user logs out

    When commissioner logs in
    And chooses to act upon the above application
    And he approved the property with remarks "property approved"
    And current user closes acknowledgement

    And chooses to act upon the above assessment
    And he does a digital signature
#
    Then he is notified that "Notice Generated Successfully"

    When commissioner closes acknowledgement
    And current user logs out

    And juniorAssistant logs in
    And chooses to act upon the above assessment
    And he generates a notice

#    Then the notice is generated successfuly







    Examples:
      | propertyHeaderDetails | ownerDetails | propertyAddressDetails | assessmentDetails     | amenitiesDetails | constructionTypeDetails | floorDetails |
      | residentialPrivate    | bimal        | addressOne             | assessmentNewProperty | all              | defaultConstructionType | firstFloor   |


  Scenario: addition
    Given juniorAssitant logs in







