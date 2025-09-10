@api
Feature: Data-Driven Product Creation in Fake Store API
  As a QA Automation Engineer
  I want to create multiple products using data from Excel
  So that I can validate API responses in a data-driven way

  Background:
    Given I set the Fake Store API endpoint for creating a product

  Scenario: Create multiple products from Excel
    Given I prepare the product payload from Excel file "Users.xlsx" and sheet "FakeApi"
    When I send POST requests for all products
    Then each product response status code should be 201 and match the payload