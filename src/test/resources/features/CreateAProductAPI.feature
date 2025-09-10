@api
Feature: Create a Product in Fake Store API
  As a QA Automation Engineer
  I want to send a POST request to create a product
  So that I can validate the API response

  Scenario: Create a new product
    Given I set the Fake Store API endpoint for creating a product
    And I prepare the product payload with:
      | title       | test product |
      | price       | 29.99        |
      | description | This is a test product |
      | category    | electronics |
      | image       | https://i.pravatar.cc |
    When I send a POST request to the endpoint
    Then the product response status code should be 201
    And the response should contain the same data as the payload
