package com.automationframework.tests;
import com.automationframework.base.BaseTest;
import com.automationframework.pages.*;
import com.automationframework.util.TestDataProviders;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NegativeCheckoutTest extends BaseTest {
    @Test(dataProvider = "checkoutData",dataProviderClass = TestDataProviders.class)
    public void checkoutWithoutFirstName(String username, String password, String firstName, String lastName, String zip) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username,password);

        HomePage homePage = new HomePage(driver);
        homePage.clickFirstProduct();
        ProductPage productPage = new ProductPage(driver);
        productPage.addToCart();

        CartPage cartPage = new CartPage(driver);
        cartPage.clickCartIcon();
        cartPage.clickCheckout();

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.enterCheckoutInfo("", lastName, zip);
        checkoutPage.clickContinue();

        Assert.assertTrue(checkoutPage.getErrorMessage().contains("First Name is required"),
                "Error message not displayed for missing first name");
    }

}
