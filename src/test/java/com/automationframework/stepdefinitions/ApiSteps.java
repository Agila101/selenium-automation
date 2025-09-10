package com.automationframework.stepdefinitions;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import com.automationframework.util.ConfigReader;
import java.util.Map;
import static org.hamcrest.Matchers.equalTo;
import io.restassured.http.ContentType;

import java.util.List;
import org.testng.Assert;



public class ApiSteps {
    private Response response;

    @Given("I set the Fake Store API endpoint for all products")
    public void setFakeStoreApiEndpoint() {
        // Base URI is set in Hooks based on env
        RestAssured.basePath = "/products"; // endpoint for all products
        System.out.println("Endpoint set to: " + RestAssured.baseURI + RestAssured.basePath);
    }

    @When("I send a GET request to the endpoint")
    public void sendGetRequest() {
        response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .extract()
                .response();

        System.out.println("Response received: " + response.getBody().asString());
    }

    @Then("the response status code should be {int}")
    public void validateResponseStatusCode(int expectedStatusCode) {
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode, "Status code mismatch");
        System.out.println("Status code validated: " + expectedStatusCode);
    }

    @Then("each product in the response should have {string}, {string}, {string}, {string}, {string}, and {string}")
    public void validateProductFields(String id, String title, String price, String description, String category, String image) {
        List<Map<String, Object>> products = response.jsonPath().getList("$");

        Assert.assertFalse(products.isEmpty(), "Product list should not be empty");

        for (Map<String, Object> product : products) {
            Assert.assertTrue(product.containsKey(id), "Product missing field: " + id);
            Assert.assertTrue(product.containsKey(title), "Product missing field: " + title);
            Assert.assertTrue(product.containsKey(price), "Product missing field: " + price);
            Assert.assertTrue(product.containsKey(description), "Product missing field: " + description);
            Assert.assertTrue(product.containsKey(category), "Product missing field: " + category);
            Assert.assertTrue(product.containsKey(image), "Product missing field: " + image);
        }

        System.out.println("All products validated with required fields.");
    }


}

