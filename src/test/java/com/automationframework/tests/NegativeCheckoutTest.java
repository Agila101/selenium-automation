package com.automationframework.tests;
import com.automationframework.base.BaseTest;
import com.automationframework.pages.*;
import com.automationframework.util.TestDataProviders;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.*;
import com.automationframework.util.AllureLogger;

@Epic("E-Commerce Automation")
@Feature("Checkout Validations")
public class NegativeCheckoutTest extends BaseTest {
    @Test(dataProvider = "checkoutData",dataProviderClass = TestDataProviders.class)
    @Description("Verify that user cannot checkout without entering first name")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Checkout form validation for missing first name")
    public void checkoutWithoutFirstName(String username, String password, String firstName, String lastName, String zip) {
        AllureLogger.logText("Starting test: checkoutWithoutFirstName");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username,password);

        HomePage homePage = new HomePage(driver);
        homePage.clickFirstProduct();
        AllureLogger.logText("Clicked on first product");
        ProductPage productPage = new ProductPage(driver);
        productPage.addToCart();
        AllureLogger.logText("Product added to cart");

        CartPage cartPage = new CartPage(driver);
        cartPage.clickCartIcon();
        cartPage.clickCheckout();
        AllureLogger.logText("Navigated to checkout page");

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.enterCheckoutInfo("", lastName, zip);
        checkoutPage.clickContinue();
        AllureLogger.logText("Tried to checkout without first name");

        Assert.assertTrue(checkoutPage.getErrorMessage().contains("First Name is required"),
                "Error message not displayed for missing first name");
        AllureLogger.logText("Verified error message displayed correctly");
    }

}
