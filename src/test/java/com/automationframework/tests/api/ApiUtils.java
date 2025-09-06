package com.automationframework.tests.api;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ApiUtils {
    private static final String BASE_URI = "https://fakestoreapi.com";

    public static Response getProductList() {
        return RestAssured
                .given()
                .baseUri(BASE_URI) // example API
                .basePath("/products")
                .when()
                .get()
                .then()
                .extract()
                .response();
    }

    public static int createProduct(String name, double price) {
        Response response = RestAssured
                .given()
                .baseUri(BASE_URI)
                .basePath("/products")
                .header("Content-Type", "application/json")
                .body("{\"title\":\""+name+"\",\"price\":"+price+"}")
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .response();

        return response.jsonPath().getInt("id");
    }
    public static Response getProductById(int id) {
        return RestAssured
                .given()
                .baseUri(BASE_URI)
                .basePath("/products/" + id)
                .when()
                .get()
                .then()
                .extract()
                .response();
    }
    public static Response patchProduct(int id, String newName) {
        return RestAssured
                .given()
                .baseUri(BASE_URI)
                .basePath("/products/" + id)
                .header("Content-Type", "application/json")
                .body("{\"title\":\"" + newName + "\"}")  // only updating title here
                .when()
                .patch()
                .then()
                .extract()
                .response();
    }
    public static Response updateProduct(int id, String newName, double newPrice) {
        return RestAssured
                .given()
                .baseUri(BASE_URI)
                .basePath("/products/" + id)
                .header("Content-Type", "application/json")
                .body("{\"title\":\"" + newName + "\",\"price\":" + newPrice + "}")
                .when()
                .put()
                .then()
                .extract()
                .response();
    }

    // Delete a product by ID
    public static void deleteProduct(int id) {
        RestAssured
                .given()
                .baseUri(BASE_URI)
                .basePath("/products/" + id)
                .when()
                .delete()
                .then()
                .statusCode(200);
    }

    // Add product to cart via API (if API supports carts)
    public static Response addProductToCart(int productId, int userId, int quantity) {
        return RestAssured
                .given()
                .baseUri(BASE_URI)
                .basePath("/carts")
                .header("Content-Type", "application/json")
                .body("{\"userId\": "+userId+", \"products\":[{\"productId\":"+productId+",\"quantity\":1}]}")
                .when()
                .post()
                .then()
                .extract()
                .response();
    }

    public static Response getCartByUser(int userId) {
        return RestAssured
                .given()
                .baseUri(BASE_URI)
                .basePath("/carts/user/" + userId)
                .when()
                .get()
                .then()
                .extract()
                .response();
    }

    public static void removeProductFromCart(int cartId, int productId) {
        // optional if API supports removing from cart
    }

}

