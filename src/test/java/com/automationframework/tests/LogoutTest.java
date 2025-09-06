package com.automationframework.tests;
import com.automationframework.base.BaseTest;
import com.automationframework.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.automationframework.util.TestDataProviders;
import com.automationframework.util.ConfigReader;

public class LogoutTest extends BaseTest {
    @Test(dataProvider = "userData",dataProviderClass = TestDataProviders.class)
    public void logoutTest(String username, String password) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username,password);

        HomePage homePage = new HomePage(driver);
        Assert.assertTrue(homePage.isProductListDisplayed(), "Login failed");

        // Perform logout
        homePage.logout();

        // Verify we are back on login page
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(), "Login page not displayed after logout");
    }
    @Test(dataProvider = "userData", dataProviderClass = TestDataProviders.class)
    public void sessionInvalidAfterLogout(String username, String password) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username, password);

        HomePage homePage = new HomePage(driver);
        Assert.assertTrue(homePage.isProductListDisplayed(), "Login failed");

        // Logout
        homePage.logout();
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(), "Not on login page after logout");

        String baseUrl = ConfigReader.getProperty("baseUrl");

        // Try navigating directly to product page
        driver.get(baseUrl + "/inventory.html");
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(), "Accessed inventory without login");

        // Try navigating directly to cart page
        driver.get(baseUrl + "/cart.html");
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(), "Accessed cart without login");
    }
    @Test(dataProvider = "userData", dataProviderClass = TestDataProviders.class)
    public void multipleLogoutsTest(String username, String password) {
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = new HomePage(driver);

        // Step 1: Login
        loginPage.login(username, password);
        Assert.assertTrue(homePage.isProductListDisplayed(), "Login failed for user: " + username);

        // Step 2: First logout
        homePage.logout();
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(), "Login page not displayed after first logout");

        // Step 3: Attempt second logout without logging in again
        try {
            homePage.logout();
        } catch (Exception e) {
            System.out.println("Second logout attempt handled gracefully: " + e.getMessage());
        }

        // Step 4: Verify login page is still displayed
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(), "Login page not displayed after second logout attempt");

        // Step 5: Optionally, try accessing a protected page and verify redirect to login
        driver.get(ConfigReader.getProperty("baseUrl") + "/inventory.html");
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(), "User was able to access protected page after logout");
    }
}
