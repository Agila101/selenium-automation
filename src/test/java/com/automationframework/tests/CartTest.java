package com.automationframework.tests;
import com.automationframework.base.BaseTest;
import com.automationframework.pages.*;
import com.automationframework.util.ExcelUtil;
import org.testng.Assert;
import org.testng.annotations.*;
import com.automationframework.util.TestDataProviders;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CartTest extends BaseTest{
    private static final Logger logger = LogManager.getLogger(CartTest.class);
    @Test(dataProvider = "userDataTwo",dataProviderClass = TestDataProviders.class)
    public void multipleProductsCartTest(String username, String password) {
        logger.info("Starting test: multipleProductsCartTest for user '{}'", username);
        // Login
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username, password);
        logger.info("Logged in successfully as '{}'", username);

        HomePage homePage = new HomePage(driver);
        Assert.assertTrue(homePage.isProductListDisplayed(), "Products not displayed");
        logger.info("Product list displayed");

        // Add multiple products
        homePage.addProductToCartByIndex(0);
        homePage.addProductToCartByIndex(1);
        homePage.addProductToCartByIndex(2);
        logger.info("Added 3 products to cart");

        // Open Cart
        CartPage cartPage = new CartPage(driver);
        cartPage.clickCartIcon();

        // Assert cart count
        Assert.assertEquals(cartPage.getCartItemsCount(), 3, "Cart items count mismatch");
        logger.info("Cart count verified: 3 items");

        // Remove a product
        cartPage.removeProductByIndex(1);
        Assert.assertEquals(cartPage.getCartItemsCount(), 2, "Cart items count after removal mismatch");
        logger.info("Removed 1 product, cart now has 2 items");

        logger.info("Test completed: multipleProductsCartTest for user '{}'", username);
    }



    @Test(dataProvider = "usersWithProducts",dataProviderClass = TestDataProviders.class)
    public void addProductsAndVerifyCartCount(String username, String password, List<String> products) {
        logger.info("Starting test: addProductsAndVerifyCartCount for user '{}'", username);

        // Login
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(username, password);
        logger.info("Logged in successfully as '{}'", username);
       ProductPage productPage=new ProductPage(getDriver());
        // Add products
        for (String product : products) {
            productPage.addProductToCartByName(product);
            logger.info("Added product '{}' to cart", product);
        }

        // Verify cart count
        int expectedCount = products.size();
        int actualCount = productPage.getCartCount();
        Assert.assertEquals(actualCount, expectedCount, "Cart count mismatch after adding products!");
        logger.info("Cart count verified: {} items", actualCount);
        logger.info("Test completed: addProductsAndVerifyCartCount for user '{}'", username);
    }


    @Test(dataProvider = "usersWithProducts", dataProviderClass = TestDataProviders.class)
    public void removeProductsAndVerifyCartEmpty(String username, String password, List<String> products) {
        logger.info("Starting test: removeProductsAndVerifyCartEmpty for user '{}'", username);
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(username, password);
        logger.info("Logged in successfully as '{}'", username);
        ProductPage productPage = new ProductPage(getDriver());

        // Add all products first
        for (String product : products) {
            productPage.addProductToCartByName(product);
            logger.info("Added product '{}' to cart", product);
        }

        // Remove all products
        for (String product : products) {
            productPage.removeProductFromCartByName(product);
        }
        int cartCount = productPage.getCartCount();
        Assert.assertEquals(cartCount, 0, "Cart should be empty after removing all products!");
        logger.info("Cart is empty after removing all products");

        logger.info("Test completed: removeProductsAndVerifyCartEmpty for user '{}'", username);

    }
        @Test(dataProvider = "usersWithProducts", dataProviderClass = TestDataProviders.class)
        public void verifyTotalPriceCalculation(String username, String password, List<String> products) {
            logger.info("Starting test: verifyTotalPriceCalculation for user '{}'", username);
            LoginPage loginPage = new LoginPage(getDriver());
            loginPage.login(username, password);
            logger.info("Logged in successfully as '{}'", username);
            ProductPage productPage = new ProductPage(getDriver());

            double expectedTotal = 0.0;

            // Add products and calculate expected total
            for (String product : products) {
                productPage.addProductToCartByName(product);
                double price = productPage.getProductPriceByName(product);
                expectedTotal += productPage.getProductPriceByName(product);
                logger.info("Added '{}' priced at {} to cart, running total: {}", product, price, expectedTotal);
            }

            // Go to cart page to get total price
            double actualTotal = productPage.getCartTotalPrice();
            Assert.assertEquals(actualTotal, expectedTotal, 0.01, "Total price mismatch in cart!");
            logger.info("Total price verified: expected = {}, actual = {}", expectedTotal, actualTotal);

            logger.info("Test completed: verifyTotalPriceCalculation for user '{}'", username);
        }
        }





