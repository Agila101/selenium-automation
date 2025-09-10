@ui
Feature: Swag Labs Purchase Flow

  Scenario: Complete purchase flow in Swag Labs
    Given I am on the Swag Labs login page
    When I login with username "standard_user" and password "secret_sauce"
    And I add the first product to the cart
    And I proceed to checkout with first name "Agila" last name "E" postal code "12345"
    Then I should see the order confirmation message