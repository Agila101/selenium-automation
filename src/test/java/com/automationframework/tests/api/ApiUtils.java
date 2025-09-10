package com.automationframework.tests.api;

import com.automationframework.util.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.File;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import io.qameta.allure.Step;
import com.automationframework.util.AllureLogger;



import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import org.testng.annotations.*;
@Listeners(com.automationframework.Listeners.TestListener.class)
public class ApiUtils {

    public static String getBaseUri() {
        return ConfigReader.getApiProperty("baseUrl");
    }

    public static String getToken() {
        return ConfigReader.getApiProperty("token");
    }

    @Step("Creating order for cartId: {cartId} and user: {username}")
    public static Response createOrder(int cartId, String username) {
        String payload = "{ \"user\": \"" + username + "\", \"cartId\": " + cartId + ", \"date\": \"" + LocalDate.now() + "\" }";

        Response response = given()
                .baseUri(getBaseUri())
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/orders")
                .then()
                .extract()
                .response();

        AllureLogger.logText("CreateOrder Response Status: " + response.getStatusCode());
        AllureLogger.logText("CreateOrder Response Body: " + response.getBody().asString());
        logResponse(response);

        return response;

    }
    public static Response createProductWithToken() {
        // Replace with your actual API call
        return RestAssured.given()
                .header("Authorization", "Bearer YOUR_TOKEN")
                .body("{\"name\":\"Test Product\",\"price\":100}")
                .post("https://yourapi.com/products");
    }

    public static Response getProductById(String productId) {
        return RestAssured.given()
                .header("Authorization", "Bearer YOUR_TOKEN")
                .get("https://yourapi.com/products/" + productId);
    }

    public static Response deleteProductById(String productId) {
        return RestAssured.given()
                .header("Authorization", "Bearer YOUR_TOKEN")
                .delete("https://yourapi.com/products/" + productId);
    }
    public static Response getProducts(String token) {
        return given()
                .baseUri(getBaseUri())
                .header("Authorization", "Bearer " + token)
                .get("/products")
                .then()
                .extract()
                .response();
    }
    public static Response createProduct(String token, String title, double price) {
        String payload = "{\"title\":\"" + title + "\",\"price\":" + price + "}";
        return given()
                .baseUri(getBaseUri())
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/products")
                .then()
                .extract()
                .response();
    }

    public static Response deleteProduct(String token, int productId) {
        return given()
                .baseUri(getBaseUri())
                .header("Authorization", "Bearer " + token)
                .delete("/products/" + productId)
                .then()
                .extract()
                .response();
    }


    @Step("Fetching order details for orderId: {orderId}")
    public static Response getOrderById(int orderId) {
        Response response = given()
                .baseUri(getBaseUri())
                .basePath("/orders/" + orderId)
                .get()
                .then()
                .extract()
                .response();

        AllureLogger.logText("GetOrderById Response Status: " + response.getStatusCode());
        AllureLogger.logText("GetOrderById Response Body: " + response.getBody().asString());
        logResponse(response);

        return response;
    }

    // ---------------------------
    // Authentication / Token
    // ---------------------------
    public static String loginAndGetToken(String username, String password) {
        Response response = given()
                .baseUri(getBaseUri())
                .basePath("/auth/login")
                .contentType(ContentType.JSON)
                .body("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
                .post()
                .then()
                .extract()
                .response();

        assertStatusCode(response, 200);
        logResponse(response);
        return response.jsonPath().getString("token");
    }

    // ---------------------------
    // Product APIs
    // ---------------------------
    public static int createProduct(String title, double price) {
        Response response = given()
                .baseUri(getBaseUri())
                .contentType(ContentType.JSON)
                .body("{\"title\":\"" + title + "\", \"price\":" + price + "}")
                .post("/products")
                .then()
                .statusCode(201)
                .extract()
                .response();

        logResponse(response);
        return response.jsonPath().getInt("id");
    }
    public static Response createOrder(int userId, String date, List<Map<String, Object>> products) {
        Map<String, Object> body = Map.of(
                "userId", userId,
                "date", date,
                "products", products
        );

        return RestAssured
                .given()
                .baseUri(ApiEndpoints.BASE_URL)
                .contentType(ContentType.JSON)
                .body(body)
                .post(ApiEndpoints.CREATE_ORDER)
                .then()
                .extract()
                .response();
    }





    @Step("Sending POST request to /products with body: {jsonPayload}")
    public static Response createProductWithRawBody(String jsonPayload) {
        Response response = given()
                .baseUri(getBaseUri())
                .contentType(ContentType.JSON)
                .body(jsonPayload)
                .post("/products")
                .then()
                .extract()
                .response();
        AllureLogger.logText("Response Status: " + response.getStatusCode());
        AllureLogger.logText("Response Body: " + response.getBody().asString());
        logResponse(response);
        return response;
    }
    public static String getTokenByRole(String role) {
        // role = "standard" or "admin"
        return ConfigReader.getApiProperty(role + "_token");
    }


    public static Response createProductWithBoundaryPrice(double price) {
        String payload = "{\"title\":\"Boundary Product\",\"price\":" + price + "}";
        Response response = given()
                .baseUri(getBaseUri())
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/products")
                .then()
                .extract()
                .response();

        logResponse(response);
        return response;
    }

    public static Response getProductById(int id) {
        Response response = given()
                .baseUri(getBaseUri())
                .basePath("/products/" + id)
                .get()
                .then()
                .extract()
                .response();

        logResponse(response);
        return response;
    }

    public static Response getProductList() {
        Response response = given()
                .baseUri(getBaseUri())
                .basePath("/products")
                .get()
                .then()
                .extract()
                .response();

        logResponse(response);
        return response;
    }

    public static Response updateProduct(int id, String name, double price) {
        String payload = "{\"title\":\"" + name + "\",\"price\":" + price + "}";
        Response response = given()
                .baseUri(getBaseUri())
                .basePath("/products/" + id)
                .contentType(ContentType.JSON)
                .body(payload)
                .put()
                .then()
                .extract()
                .response();

        logResponse(response);
        return response;
    }

    public static Response patchProduct(int id, String name) {
        String payload = "{\"title\":\"" + name + "\"}";
        Response response = given()
                .baseUri(getBaseUri())
                .basePath("/products/" + id)
                .contentType(ContentType.JSON)
                .body(payload)
                .patch()
                .then()
                .extract()
                .response();

        logResponse(response);
        return response;
    }

    public static Response deleteProduct(int id) {
        Response response = given()
                .baseUri(getBaseUri())
                .basePath("/products/" + id)
                .delete()
                .then()
                .extract()
                .response();

        assertStatusCode(response, 200);
        logResponse(response);
        return response;
    }

    // ---------------------------
    // Cart APIs
    // ---------------------------
    public static Response addProductToCart(int productId, int userId, int quantity) {
        String payload = "{ \"userId\": " + userId + ", \"date\": \"" + LocalDate.now() + "\", " +
                "\"products\": [{\"productId\": " + productId + ", \"quantity\": " + quantity + "}]}";

        Response response = given()
                .baseUri(getBaseUri())
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/carts")
                .then()
                .extract()
                .response();

        logResponse(response);
        return response;
    }

    public static Response getCartByUser(int userId) {
        Response response = given()
                .baseUri(getBaseUri())
                .basePath("/carts/user/" + userId)
                .get()
                .then()
                .extract()
                .response();

        logResponse(response);
        return response;
    }

    // ---------------------------
    // Utilities
    // ---------------------------
    public static boolean isProductNotFound(int productId) {
        Response response = getProductById(productId);
        return response.statusCode() == 404 || response.getBody().asString().toLowerCase().contains("not found") || response.getBody().asString().isEmpty();
    }

    public static void validateProductListSchema(String schemaPath) {
        getProductList().then().assertThat().body(matchesJsonSchemaInClasspath(schemaPath));
    }

    public static void validatePayloadAgainstSchema(String jsonBody, String schemaPath) {
        File schemaFile = new File(ApiUtils.class.getClassLoader().getResource(schemaPath).getFile());
        org.hamcrest.MatcherAssert.assertThat(jsonBody, matchesJsonSchema(schemaFile));
    }

    public static void assertStatusCode(Response response, int expected) {
        if (response.statusCode() != expected) {
            throw new AssertionError("Expected status code: " + expected + ", but got: " + response.statusCode());
        }
    }

    public static void logResponse(Response response) {
        System.out.println("==== API CALL LOG ====");
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.getBody().asString());
        System.out.println("======================\n");
    }

    public static Response sendGetToInvalidEndpoint(String endpoint) {
        Response response = given()
                .baseUri(getBaseUri())
                .get(endpoint)
                .then()
                .extract()
                .response();

        logResponse(response);
        return response;
    }
}
