package com.automationframework.tests;

import com.automationframework.base.BaseTest;
import com.automationframework.pages.*;
import com.automationframework.Listeners.RetryAnalyzer;
import com.automationframework.util.ExcelUtil;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.automationframework.util.TestDataProviders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Listeners(com.automationframework.Listeners.TestListener.class)
public class CheckoutTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(CheckoutTest.class);

    @Test(dataProvider = "checkoutData",dataProviderClass = TestDataProviders.class)
    public void completeCheckoutTest(String username, String password, String firstName, String lastName, String zip) {
        logger.info("Logging in as: {}", username);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username, password);

        HomePage homePage = new HomePage(driver);
        Assert.assertTrue(homePage.isProductListDisplayed(), "Login failed or product list not visible for: " + username);

        logger.info("Clicking first product");
        homePage.clickFirstProduct();

        ProductPage productPage = new ProductPage(driver);
        logger.info("Adding product to cart");
        productPage.addToCart();



        CartPage cartPage = new CartPage(driver);
        logger.info("Clicking cart icon and checkout");
        cartPage.clickCartIcon();
        cartPage.clickCheckout();

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        logger.info("Entering checkout info");
        checkoutPage.enterCheckoutInfo(firstName, lastName, zip);
        checkoutPage.clickContinue();
        logger.info("Clicking finish");
        checkoutPage.clickFinish();

        Assert.assertTrue(checkoutPage.getConfirmationMessage().contains("Thank you for your order"),
                "Checkout confirmation not displayed");
        logger.info("Checkout completed successfully for: {}", username);
    }
    @Test(dataProvider = "excelUserProductDataEdge", dataProviderClass = TestDataProviders.class)
    public void checkoutNegativeFlowTest(String username, String password, String firstName, String lastName, String zip, String productTitle) {

        // Login
        logger.info("Logging in as: {}", username);
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(username, password);

        // Verify login
        HomePage homePage = new HomePage(getDriver());
        Assert.assertTrue(homePage.isProductListDisplayed(), "Login failed for user: " + username);

        // Add product to cart
        logger.info("Adding product to cart: {}", productTitle);
        homePage.clickProductByName(productTitle);
        ProductPage productPage = new ProductPage(getDriver());
        productPage.addToCart();

        // Go to cart
        CartPage cartPage = new CartPage(getDriver());
        cartPage.clickCartIcon();
        Assert.assertTrue(cartPage.isProductInCart(productTitle), "Product not added to cart: " + productTitle);

        // Checkout
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(getDriver());
        checkoutPage.enterCheckoutInfo(firstName, lastName, zip);
        checkoutPage.clickContinue();

        // Negative flow validation
        if (firstName == null || firstName.isEmpty()) {
            Assert.assertTrue(checkoutPage.getErrorMessage().contains("First Name is required"), "First name validation failed");
            logger.warn("First name validation failed for user: {}", username);
        } else if (lastName == null || lastName.isEmpty()) {
            Assert.assertTrue(checkoutPage.getErrorMessage().contains("Last Name is required"), "Last name validation failed");
            logger.warn("Last name validation failed for user: {}", username);
        } else if (zip == null || zip.isEmpty()) {
            Assert.assertTrue(checkoutPage.getErrorMessage().contains("Postal Code is required"), "ZIP code validation failed");
            logger.warn("ZIP code validation failed for user: {}", username);
        } else {
            // Edge cases (long names, special characters)
            checkoutPage.clickFinish();
            Assert.assertTrue(checkoutPage.getConfirmationMessage().contains("Thank you for your order"),
                    "Checkout confirmation not displayed for edge case: " + username);
            logger.info("Checkout completed successfully for edge case: {}", username);

        }
    }

}
