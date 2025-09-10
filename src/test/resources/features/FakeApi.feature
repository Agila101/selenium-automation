@api
Feature: Fetch Products from Fake Store API
  As a QA Automation Engineer
  I want to fetch products using GET API
  So that I can validate the response structure and status

  Scenario: Get all products and validate response
    Given I set the Fake Store API endpoint for all products
    When I send a GET request to the endpoint
    Then the response status code should be 200
    And each product in the response should have "id", "title", "price", "description", "category", and "image"
