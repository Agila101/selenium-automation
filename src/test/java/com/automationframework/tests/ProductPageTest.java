package com.automationframework.tests;

import com.automationframework.base.BaseTest;
import com.automationframework.base.SaucelabsBaseTest;
import com.automationframework.pages.*;
import com.automationframework.util.ExcelUtil;
import org.testng.Assert;
import org.testng.annotations.*;
import org.openqa.selenium.*;
import com.automationframework.util.TestDataProviders;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import com.automationframework.util.AllureLogger;


@Listeners(com.automationframework.Listeners.TestListener.class)
public class ProductPageTest extends BaseTest {


    @Test(dataProvider = "userData",dataProviderClass = TestDataProviders.class)
    public void addProductToCartTest(String username, String password) {
        // Login
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(username, password);

        // Navigate to Home
        HomePage homePage = new HomePage(getDriver());
        Assert.assertTrue(homePage.isProductListDisplayed(), "Products not visible on home page");

        // Click first product
        homePage.clickFirstProduct();

        // Product page actions
        ProductPage productPage = new ProductPage(getDriver());
        productPage.addToCart();
        Assert.assertTrue(productPage.isRemoveButtonDisplayed(), "Remove button not displayed after adding to cart");

        productPage.removeFromCart();
        Assert.assertTrue(productPage.isAddToCartButtonDisplayed(), "Add to cart button not displayed after removing product");

        productPage.goBack();
    }
    @Test(dataProvider = "userData",dataProviderClass = TestDataProviders.class)
    public void verifyProductDetails(String username, String password) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username,password);

        HomePage homePage = new HomePage(driver);
        homePage.clickFirstProduct();

        ProductPage productPage = new ProductPage(driver);
        Assert.assertTrue(productPage.isAddToCartButtonDisplayed(), "Add to cart button missing");
        Assert.assertTrue(productPage.getProductTitle().length() > 0, "Product title missing");
        Assert.assertTrue(productPage.getProductPrice() > 0, "Product price invalid");
    }
    @Test(dataProvider = "userData",dataProviderClass = TestDataProviders.class)
    public void sortProductsTest(String username, String password) {
        // Login first
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username, password);

        HomePage homePage = new HomePage(driver);
        Assert.assertTrue(homePage.isProductListDisplayed(), "Products not visible on home page");

        // Sort by Price: Low to High
        homePage.sortProducts("Price (low to high)");

        // Verify products are sorted (simple check: first product price <= second product price)
        double firstPrice = homePage.getProductPrice(homePage.getAllProductTitles().get(0));
        double secondPrice = homePage.getProductPrice(homePage.getAllProductTitles().get(1));
        Assert.assertTrue(firstPrice <= secondPrice, "Products not sorted correctly by price");

        // Sort by Name: A-Z
        homePage.sortProducts("Name (A to Z)");

        String firstProductName = homePage.getAllProductTitles().get(0);
        String secondProductName = homePage.getAllProductTitles().get(1);
        Assert.assertTrue(firstProductName.compareToIgnoreCase(secondProductName) <= 0,
                "Products not sorted correctly by name");
    }
    @Test(dataProvider = "excelUserProductData", dataProviderClass = TestDataProviders.class)
    @Description("Verify user can select a product by its title, add to cart, and remove it successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void selectProductByTitleTest(String username, String password,
                                         String firstName, String lastName,
                                         String zip, String productTitle) {
        AllureLogger.logText("Starting test: selectProductByTitleTest");
        // Login first
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username, password);
        AllureLogger.logText("Logged in as user: " + username);

        HomePage homePage = new HomePage(driver);
        Assert.assertTrue(homePage.isProductListDisplayed(), "Products not visible on home page");
        AllureLogger.logText("Verified product list is visible on home page");
        // Select a specific product by title
        boolean clicked = homePage.selectProductByTitle(productTitle);
        Assert.assertTrue(clicked, "Product not found: " + productTitle);
        AllureLogger.logText("Selected product with title: " + productTitle);

        // Verify product details
        ProductPage productPage = new ProductPage(driver);
        Assert.assertEquals(productPage.getProductTitle(), productTitle, "Product title mismatch");
        Assert.assertTrue(productPage.getProductPrice() > 0, "Product price invalid");
        AllureLogger.logText("Verified product title and price");
        productPage.addToCart();
        Assert.assertTrue(productPage.isRemoveButtonDisplayed(), "Remove button not displayed after adding to cart");
        AllureLogger.logText("Added product to cart");
        // Remove from cart
        productPage.removeFromCart();
        Assert.assertTrue(productPage.isAddToCartButtonDisplayed(), "Add to cart button not displayed after removing product");
        AllureLogger.logText("Removed product from cart");
        // Go back to home page
        productPage.goBack();
        AllureLogger.logText("Returned to home page");

        AllureLogger.logText("Completed test: selectProductByTitleTest successfully");
    }

}