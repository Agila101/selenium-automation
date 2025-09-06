package com.automationframework.tests;

import com.automationframework.base.BaseTest;
import com.automationframework.pages.LoginPage;
import com.automationframework.util.ConfigReader;
import com.automationframework.util.ExcelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.automationframework.util.TestDataProviders;

public class LoginTest extends BaseTest {

    private LoginPage loginPage;
    private static final Logger logger = LogManager.getLogger(LoginTest.class);
    private static ExcelUtil excel = new ExcelUtil("Users.xlsx");

    @BeforeMethod(alwaysRun = true)
    public void setupPage() {
        loginPage = new LoginPage(getDriver());
        logger.info("LoginPage initialized");
    }
    @Test
    public void loginWithEmptyFields() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("", "");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Username is required"), "Error message missing");
    }


    @Test(dataProvider = "loginData",dataProviderClass = TestDataProviders.class)
    public void loginTest(String username, String password, String expectedType, String expectedError) {
        logger.info("Testing login for user: " + username + " | Expected: " + expectedType);

        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();

        if (expectedType.equalsIgnoreCase("valid")) {
            String actualTitle = loginPage.getProductsTitle();
            logger.info("Actual page title: " + actualTitle);
            Assert.assertEquals(actualTitle.toUpperCase(), "PRODUCTS", "Page title mismatch for valid login");
        } else if (expectedType.equalsIgnoreCase("invalid")) {
            String error = loginPage.getErrorMessage();
            logger.info("Error message displayed: " + error);
            Assert.assertTrue(error.contains(expectedError), "Error message mismatch");
        }
    }
}