@ui
Feature: Swag Labs Checkout and Sorting with Excel Data
  As a user
  I want to automate Swag Labs checkout and product sorting
  Using data from Excel sheets

  @ui
  Scenario: Checkout using Excel data
    Given I load Swag Labs test data from Excel file "Users.xlsx"
    And I am on the Swag Labs Excel login page
    When I login for "Checkout" scenario with Excel credentials
    And I add product(s) from Excel sheet "BDDSwagProducts" to the cart
    And I complete checkout using data from "BDDSwagCheckout"
    Then I should see excel order confirmation message

  @ui
  Scenario: Sort products using Excel data
    Given I load Swag Labs test data from Excel file "Users.xlsx"
    And I am on the Swag Labs Excel login page
    When I login for "Sorting" scenario with Excel credentials
    And I apply all sorting options from "BDDSwagSorting"
    Then I should see products sorted correctly