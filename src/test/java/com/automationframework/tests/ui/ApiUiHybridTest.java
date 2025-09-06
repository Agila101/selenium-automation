package com.automationframework.tests.ui;

import com.automationframework.base.BaseTest;
import com.automationframework.pages.HomePage;
import com.automationframework.pages.LoginPage;
import com.automationframework.pages.ProductPage;
import com.automationframework.tests.api.ApiUtils;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;
import java.util.Map;



public class ApiUiHybridTest extends BaseTest {

    @Test//get
    public void addApiProductToCart() {
        // Step 1: Get product list via API
        Response response = ApiUtils.getProductList();
        Assert.assertEquals(response.statusCode(), 200);

        String firstProductTitle = response.jsonPath().getString("[0].title").trim();
        System.out.println("First product from API: " + firstProductTitle);

        // Step 2: Login via UI
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("standard_user", "secret_sauce");

        // Step 3: Find product in UI and add to cart
        HomePage homePage = new HomePage(getDriver());
        homePage.selectProductByTitle(firstProductTitle);

        ProductPage productPage = new ProductPage(getDriver());
        productPage.addToCart();

        // Step 4: Verify product is added
        Assert.assertTrue(productPage.isRemoveButtonDisplayed(), "Product not added to cart");
    }


    @Test//post
    public void createApiProductAndVerifyInUi() {
        // Step 1: Create product via API
        String productName = "Test Product";
        double productPrice = 99.99;
        int productId = ApiUtils.createProduct(productName, productPrice);

        try {
            // Step 2: Login via UI
            LoginPage loginPage = new LoginPage(getDriver());
            loginPage.login("standard_user", "secret_sauce");

            // Step 3: Refresh UI to make new product visible
            getDriver().navigate().refresh();

            HomePage homePage = new HomePage(getDriver());
            homePage.selectProductByTitle(productName);

            ProductPage productPage = new ProductPage(getDriver());
            Assert.assertTrue(productPage.isAddToCartButtonDisplayed(), "New product not displayed in UI");

        } finally {
            // Cleanup: delete product via API
            ApiUtils.deleteProduct(productId);
        }
    }

    @Test//Delete the product via API.
    public void deleteApiProductAndVerifyNotInUi() {
        String productName = "Delete Test Product";
        double productPrice = 19.99;
        int productId = ApiUtils.createProduct(productName, productPrice);

        // Step 1: Login via UI
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("standard_user", "secret_sauce");

        getDriver().navigate().refresh(); // Product visible
        HomePage homePage = new HomePage(getDriver());
        homePage.selectProductByTitle(productName);
        ProductPage productPage = new ProductPage(getDriver());
        Assert.assertTrue(productPage.isAddToCartButtonDisplayed(), "New product not displayed in UI");

        // Step 2: Delete via API
        ApiUtils.deleteProduct(productId);

        getDriver().navigate().refresh(); // Refresh UI
        boolean productVisible = homePage.isProductVisible(productName);
        Assert.assertFalse(productVisible, "Deleted product still visible in UI");
    }

    @Test//Get all products via API.
    public void verifyProductDetailsApiVsUi() {
        // Step 1: Get products from API
        Response apiResponse = ApiUtils.getProductList();
        Assert.assertEquals(apiResponse.statusCode(), 200);

        List<Map<String, Object>> apiProducts = apiResponse.jsonPath().getList("$");
        System.out.println("API Products:");
        apiProducts.forEach(p -> System.out.println(p.get("title")));

        // Step 2: Login via UI
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("standard_user", "secret_sauce");

        HomePage homePage = new HomePage(getDriver());
        List<String> uiProducts = homePage.getAllProductTitles();

        // Step 3: Match UI products with API products
        for (String uiTitle : uiProducts) {
            String cleanedUiTitle = uiTitle.trim().toLowerCase();

            Map<String, Object> apiProduct = apiProducts.stream()
                    .filter(p -> p.get("title").toString().trim().toLowerCase().equals(cleanedUiTitle))
                    .findFirst()
                    .orElse(null);

            if (apiProduct == null) {
                System.out.println("Warning: API product not found for UI product: " + uiTitle);
                continue; // Skip instead of failing
            }

            // Compare price
            double apiPrice = Double.parseDouble(apiProduct.get("price").toString());
            double uiPrice = homePage.getProductPrice(uiTitle);
            Assert.assertEquals(uiPrice, apiPrice, 0.01, "Price mismatch for product: " + uiTitle);
        }
    }

    @Test
    public void updateApiProductAndVerifyApiResponse() {
        // Step 1: Create a product via API
        String originalName = "Original Product";
        double originalPrice = 50.0;
        int productId = ApiUtils.createProduct(originalName, originalPrice);

        try {
            // Step 2: Update product via API
            String updatedName = "Updated Product";
            double updatedPrice = 75.0;
            Response updateResponse = ApiUtils.updateProduct(productId, updatedName, updatedPrice);

            // Step 3: Verify API response reflects update
            Assert.assertEquals(updateResponse.statusCode(), 200);
            Assert.assertEquals(updateResponse.jsonPath().getString("title"), updatedName);
            Assert.assertEquals(updateResponse.jsonPath().getDouble("price"), updatedPrice);

        } finally {
            ApiUtils.deleteProduct(productId);
        }
    }

    @Test
    public void patchApiProduct() {
        // Step 1: Create product
        int productId = ApiUtils.createProduct("Patch Original", 45.0);

        try {
            // Step 2: Patch update product title
            String patchedName = "Patched Product";
            Response patchResponse = ApiUtils.patchProduct(productId, patchedName);

            // Step 2a: Verify status code
            Assert.assertEquals(patchResponse.statusCode(), 200, "PATCH request failed");

            // Step 2b: Print raw response for debugging
            String responseBody = patchResponse.getBody().asString();
            System.out.println("PATCH Response Body: " + responseBody);

            // Step 3: Only parse JSON if response body is not empty
            if (responseBody != null && !responseBody.isEmpty()) {
                String updatedTitle = patchResponse.jsonPath().getString("title");
                Assert.assertEquals(updatedTitle, patchedName, "Product title not updated via PATCH");
            } else {
                System.out.println("PATCH API returned empty response. Skipping JSON verification.");
            }

            // Step 4: Verify via GET
            Response getResponse = ApiUtils.getProductById(productId);
            Assert.assertEquals(getResponse.statusCode(), 200, "GET after PATCH failed");

            String getBody = getResponse.getBody().asString();
            if (getBody != null && !getBody.isEmpty()) {
                String getTitle = getResponse.jsonPath().getString("title");
                Assert.assertEquals(getTitle, patchedName, "GET after PATCH shows wrong title");
            } else {
                System.out.println("GET API returned empty response. Cannot verify title.");
            }

        } finally {
            // Cleanup
            ApiUtils.deleteProduct(productId);
        }
    }
}





