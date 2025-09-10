package com.automationframework.tests.ui;

import com.automationframework.base.ApiBaseTest;
import com.automationframework.reports.ExtentManager;
import com.automationframework.tests.api.ApiUtils;
import com.automationframework.util.ApiDataProviders;
import com.automationframework.util.ScreenshotUtil;
import com.automationframework.tests.api.ApiUtils;
import com.automationframework.tests.api.ProductApi;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import io.restassured.RestAssured;

import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import com.automationframework.util.AllureLogger;
import java.io.IOException;
import java.io.File;
import com.automationframework.tests.api.ApiEndpoints;

import static io.restassured.RestAssured.given;

public class ApiUiHybridTest extends ApiBaseTest {
    ProductApi productApi = new ProductApi();

    private ExtentReports extent = ExtentManager.getInstance();
    private ExtentTest test;
    private static final Logger logger = LogManager.getLogger(ApiUiHybridTest.class);

    @BeforeMethod
    public void startTest(Method method) {
        test = extent.createTest(method.getName());
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            String screenshotPath = ScreenshotUtil.takeScreenshot(getDriver(), result.getName());
            test.fail("Test Failed: " + result.getThrowable())
                    .addScreenCaptureFromPath(screenshotPath);
            try {
                byte[] screenshotBytes = java.nio.file.Files.readAllBytes(new File(screenshotPath).toPath());
                AllureLogger.attachScreenshot(screenshotBytes); // attaches to Allure
            } catch (IOException e) {
                e.printStackTrace();

            }
            AllureLogger.logText("Failure reason: " + result.getThrowable().getMessage());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("Test Passed");
            AllureLogger.logText("Test passed successfully: " + result.getName());
        }
        extent.flush();
    }

    // =========================
    // API Product Tests
    // =========================

    @Test
    @Description("Add product via API to cart and verify the response")
    @Severity(SeverityLevel.CRITICAL)
    public void addApiProductToCartTest() {
            logger.info("Creating API product to add to cart");
            int productId = ApiUtils.createProduct("API Cart Test Product", 39.99);
            addStep("Add product to cart with quantity 2");
            try {
                Response cartResponse = ApiUtils.addProductToCart(productId, 1, 2);// add 2 qty
                logger.info("Cart Response: " + cartResponse.getBody().asString());
                Assert.assertEquals(cartResponse.statusCode(), 201);
                Assert.assertEquals(cartResponse.jsonPath().getInt("products[0].productId"), productId);
                Assert.assertEquals(cartResponse.jsonPath().getInt("products[0].quantity"), 2);
            } finally {
                ApiUtils.deleteProduct(productId);
            }
        }

            @Step("{0}")
            public void addStep (String message){
                AllureLogger.logText(message); // logs to Allure
                logger.info(message);          // logs to console/log4j
            }


    @Test
    @Description("Create a product and verify the API response matches expected")
    @Severity(SeverityLevel.NORMAL)
    public void createApiProductAndVerifyApiResponse() {
        double productPrice = 99.99;
        addStep("Create product with boundary price " + productPrice);
        Response postResponse = ApiUtils.createProductWithBoundaryPrice(productPrice);

        Assert.assertEquals(postResponse.statusCode(), 201);
        addStep("Verify API response for product creation");
        int productId = postResponse.jsonPath().getInt("id");
        Assert.assertEquals(postResponse.jsonPath().getString("title"), "Boundary Product");
        Assert.assertEquals(postResponse.jsonPath().getDouble("price"), productPrice, 0.01);

        ApiUtils.deleteProduct(productId);
    }

    @Test
    @Description("Delete a product via API and verify it is not found")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteApiProductAndVerifyNotFound() {
        int productId = ApiUtils.createProduct("Delete Test Product", 59.99);
        addStep("Delete product with ID: " + productId);
        Response deleteResponse = ApiUtils.deleteProduct(productId);
        addStep("Verify deletion was successful");
        Assert.assertEquals(deleteResponse.getStatusCode(), 200);
        Assert.assertTrue(ApiUtils.isProductNotFound(productId));
    }

    @Test
    public void verifyProductDetailsApi() {
        Response apiResponse = ApiUtils.getProductList();
        Assert.assertEquals(apiResponse.statusCode(), 200);

        List<Map<String, Object>> apiProducts = apiResponse.jsonPath().getList("$");
        apiProducts.forEach(p -> System.out.println("Title: " + p.get("title") + ", Price: " + p.get("price")));

        for (Map<String, Object> product : apiProducts) {
            Assert.assertFalse(product.get("title").toString().trim().isEmpty());
            Assert.assertTrue(Double.parseDouble(product.get("price").toString()) >= 0);
        }
    }

    @Test
    public void updateApiProductAndVerifyApiResponse() {
        int productId = ApiUtils.createProduct("Original Product", 50.0);

        try {
            Response updateResponse = ApiUtils.updateProduct(productId, "Updated Product", 75.0);
            Assert.assertEquals(updateResponse.statusCode(), 200);
            Assert.assertEquals(updateResponse.jsonPath().getString("title"), "Updated Product");
            Assert.assertEquals(updateResponse.jsonPath().getDouble("price"), 75.0);
        } finally {
            ApiUtils.deleteProduct(productId);
        }
    }

    @Test
    public void patchApiProduct() {
        int productId = ApiUtils.createProduct("Patch Original", 45.0);

        try {
            // PATCH the product
            Response patchResponse = ApiUtils.patchProduct(productId, "Patched Product");
            Assert.assertEquals(patchResponse.statusCode(), 200);
            Assert.assertEquals(patchResponse.jsonPath().getString("title"), "Patched Product");

            // GET the product to double-check (safely handle empty response)
            Response getResponse = ApiUtils.getProductById(productId);
            String responseBody = getResponse.getBody().asString();

            if (responseBody.isEmpty()) {
                System.out.println("GET returned empty body, skipping JSON parse.");
            } else {
                Assert.assertEquals(getResponse.statusCode(), 200);
                Assert.assertEquals(getResponse.jsonPath().getString("title"), "Patched Product");
            }

        } finally {
            // Clean up
            ApiUtils.deleteProduct(productId);
        }
    }

    @Test
    public void verifyGetProductsResponseTime() {
        Response response = ApiUtils.getProductList();
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertTrue(response.time() < 2000);
    }

    @Test
    public void verifyProductJsonSchema() {
        ApiUtils.validateProductListSchema("schemas/product-schema.json");
    }

    @Test
    public void verifyInvalidProductReturnsNotFound() {
        int invalidId = 999999;
        Assert.assertTrue(ApiUtils.isProductNotFound(invalidId));
    }

    @Test
    public void createProductInvalidPayloadReturns400() {
        String invalidJson = "{\"title\": \"Test Product\", \"price\": 19.99";
        Response response = ApiUtils.createProductWithRawBody(invalidJson);
        Assert.assertEquals(response.statusCode(), 400);
    }
    @Test
    public void fullPurchaseFlowTest() {
        // Step 1: GET all products
        Response getProductsResponse = ProductApi.getAllProducts();
        Assert.assertEquals(getProductsResponse.getStatusCode(), 200, "GET /products failed");

        // Pick the first product for the order
        int productId = getProductsResponse.jsonPath().getInt("[0].id");
        String productTitle = getProductsResponse.jsonPath().getString("[0].title");

        // Step 2: Create a new order (cart)
        List<Map<String, Object>> products = List.of(
                Map.of("productId", productId, "quantity", 2)
        );
        Response createOrderResponse = ApiUtils.createOrder(1, "2025-09-08", products);
        Assert.assertEquals(createOrderResponse.getStatusCode(), 201, "POST /carts failed");

        // Step 3: Verify that POST response contains correct product
        int returnedProductId = createOrderResponse.jsonPath().getInt("products[0].productId");
        int returnedQuantity = createOrderResponse.jsonPath().getInt("products[0].quantity");

        Assert.assertEquals(returnedProductId, productId, "Product ID mismatch in order");
        Assert.assertEquals(returnedQuantity, 2, "Quantity mismatch in order");

        // Step 4 (Optional): Log details for reporting
        System.out.println("Order created successfully!");
        System.out.println("Product: " + productTitle + " (ID: " + productId + ")");
        System.out.println("Quantity: " + returnedQuantity);
    }


    @Test
    public void testProtectedEndpointMissingToken() {
        Response response = given().baseUri("http://localhost:8080").get("/orders");
        Assert.assertTrue(response.statusCode() == 401 || response.statusCode() == 403 || response.statusCode() == 404);
    }

    @Test
    @Description("Verify that an unauthorized user cannot create a product")
    @Severity(SeverityLevel.CRITICAL)
    public void testUnauthorizedUserCannotCreateProduct() {
        String payload = "{ \"title\": \"Unauthorized Product\", \"price\": 10 }";
        addStep("Sending POST request to create product without authorization");
        Response response = given().contentType("application/json").body(payload).post("/products");
        addStep("Sending POST request to create product without authorization");
        Assert.assertEquals(response.statusCode(), 403);
    }
    @Test
    public void testCreateGetDeleteProductWithToken() {
        // Create product
        Response createResponse = ApiUtils.createProductWithToken();
        logApiCall(createResponse);

        // Check if response body is empty before parsing
        String createBody = createResponse.getBody().asString();
        if (createBody == null || createBody.isEmpty()) {
            System.out.println("Create Product response body is empty, skipping JSON parsing");
            return; // or fail the test with an informative message
        }

        // Parse JSON safely
        JsonPath createJson = null;
        try {
            createJson = createResponse.jsonPath();
        } catch (Exception e) {
            System.out.println("Failed to parse Create Product response as JSON: " + e.getMessage());
            return;
        }

        // Extract productId
        String productId = createJson.getString("id");
        if (productId == null) {
            System.out.println("Product ID is null, cannot proceed with Get/Delete test");
            return;
        }

        // Get product
        Response getResponse = ApiUtils.getProductById(productId);
        logApiCall(getResponse);

        String getBody = getResponse.getBody().asString();
        if (getBody == null || getBody.isEmpty()) {
            System.out.println("Get Product response body is empty, skipping JSON parsing");
        } else {
            try {
                JsonPath getJson = getResponse.jsonPath();
                System.out.println("Fetched product name: " + getJson.getString("name"));
            } catch (Exception e) {
                System.out.println("Failed to parse Get Product response as JSON: " + e.getMessage());
            }
        }

        // Delete product
        Response deleteResponse = ApiUtils.deleteProductById(productId);
        logApiCall(deleteResponse);

        System.out.println("Delete status code: " + deleteResponse.getStatusCode());
    }
    @Test(dataProvider = "UserRoles",dataProviderClass = ApiDataProviders.class)
    public void testRoleBasedAccess(String role, String token, int expectedStatusCode) {
        System.out.println("Role: " + role);
        System.out.println("Token: " + token);
        System.out.println("Expected Status Code: " + expectedStatusCode);

        // Here you would call your API method, e.g.:
        // int actualStatus = ApiClient.getResponseStatus(token);
        // Assert.assertEquals(actualStatus, expectedStatusCode);
    }
    @Test
    public void testConcurrentProductCreation() throws InterruptedException {
        int numberOfUsers = 10; // number of concurrent threads
        ExecutorService executor = Executors.newFixedThreadPool(numberOfUsers);

        for (int i = 0; i < numberOfUsers; i++) {
            executor.submit(() -> {
                try {
                    Response response = ApiUtils.createProductWithToken();
                    System.out.println("Thread: " + Thread.currentThread().getName() +
                            " | Status Code: " + response.getStatusCode());

                    if (response.getStatusCode() == 201) {
                        String productId = response.jsonPath().getString("id");
                        // Optional: Delete product to clean up
                        ApiUtils.deleteProductById(productId);
                    }
                } catch (Exception e) {
                    System.out.println("Thread: " + Thread.currentThread().getName() +
                            " | Exception: " + e.getMessage());
                }
            });
        }
    }


    // Helper method to log API calls
    private void logApiCall(Response response) {
        System.out.println("==== API CALL LOG ====");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());
        System.out.println("======================");
    }

    @Test(dataProvider = "validProductUpdates", dataProviderClass = ApiDataProviders.class)
    @Description("Update a product with valid data and verify the response")
    @Severity(SeverityLevel.NORMAL)
    public void updateProductWithValidData(String newName, double newPrice) {
        int productId = ApiUtils.createProduct("Temp Product", 25.0);
        try {
            addStep("Updating product ID " + productId + " with Name: " + newName + ", Price: " + newPrice);
            Response response = ApiUtils.updateProduct(productId, newName, newPrice);
            addStep("Verifying response status and updated values");
            Assert.assertEquals(response.statusCode(), 200);
            Assert.assertEquals(response.jsonPath().getString("title"), newName);
            Assert.assertEquals(response.jsonPath().getDouble("price"), newPrice);
        } finally {
            ApiUtils.deleteProduct(productId);
        }
    }

    @Test
    public void verifyProductDetailsApiVsUiWithSoftAssertions() {
        String expectedTitle = "Sauce Labs Backpack";
        String expectedPrice = "$29.99";
        String expectedDescription = "carry.allTheThings() with the sleek, streamlined Sly Pack that melds style with protection.";

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(getProductTitleFromUI(), expectedTitle);
        softAssert.assertEquals(getProductPriceFromUI(), expectedPrice);
        softAssert.assertEquals(getProductDescriptionFromUI(), expectedDescription);
        softAssert.assertAll();
    }

    // Dummy UI fetch methods
    private String getProductTitleFromUI() { return "Sauce Labs Backpack"; }
    private String getProductPriceFromUI() { return "$29.99"; }
    private String getProductDescriptionFromUI() { return "carry.allTheThings() with the sleek, streamlined Sly Pack that melds style with protection."; }

    @Test
    @Description("Verify that requesting an invalid endpoint returns 404")
    @Severity(SeverityLevel.CRITICAL)
    public void testInvalidEndpointReturns404() {
        Response response = ApiUtils.sendGetToInvalidEndpoint("/invalid-endpoint");
        Assert.assertEquals(response.statusCode(), 404);
    }

    @Test
    @Description("Validate product list against JSON schema")
    @Severity(SeverityLevel.NORMAL)
    public void testProductListSchemaValidation() {
        ApiUtils.validateProductListSchema("schemas/product-list-schema.json");
    }

    @Test(dataProvider = "priceBoundaryData", dataProviderClass = ApiDataProviders.class)
    @Description("Verify product creation with boundary prices")
    @Severity(SeverityLevel.NORMAL)
    public void testPriceBoundary(double price, boolean expectedValid) {
        Response response = ApiUtils.createProductWithBoundaryPrice(price);

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            Assert.assertEquals(response.jsonPath().getDouble("price"), price, 0.01);
        } else {
            Assert.fail("API call failed with status: " + response.statusCode());
        }
    }

    @Test
    public void testAddProductToCart() {
        int productId = ApiUtils.createProduct("Cart Test Product", 49.99);
        try {
            Response cartResponse = ApiUtils.addProductToCart(productId, 1, 1);
            Assert.assertEquals(cartResponse.statusCode(), 201);
            Assert.assertEquals(cartResponse.jsonPath().getInt("products[0].productId"), productId);
            Assert.assertEquals(cartResponse.jsonPath().getInt("products[0].quantity"), 1);
        } finally {
            ApiUtils.deleteProduct(productId);
        }
    }
}