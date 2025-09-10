package com.automationframework.tests.api;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ProductApi {
    public static Response getAllProducts() {
        return RestAssured
                .given()
                .baseUri(ApiEndpoints.BASE_URL)
                .get(ApiEndpoints.GET_PRODUCTS)
                .then()
                .extract()
                .response();
    }
    public static Response getProductById(String productId) {
        return RestAssured
                .given()
                .baseUri(ApiEndpoints.BASE_URL)
                .pathParam("id", productId)
                .when()
                .get("/products/{id}")
                .then()
                .extract()
                .response();
    }
}
