package com.automationframework.stepdefinitions;
import com.automationframework.util.DriverFactory;
import io.cucumber.java.en.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class SwagLabSteps {
    private WebDriver driver = DriverFactory.getDriver();

    @Given("I am on the Swag Labs login page")
    public void i_am_on_login_page() {
        driver.get("https://www.saucedemo.com/");
    }

    @When("I login with username {string} and password {string}")
    public void i_login(String username, String password) {
        driver.findElement(By.id("user-name")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("login-button")).click();
    }

    @When("I add the first product to the cart")
    public void i_add_first_product() {
        driver.findElement(By.cssSelector(".inventory_item button")).click();
        driver.findElement(By.className("shopping_cart_link")).click();
    }

    @When("I proceed to checkout with first name {string} last name {string} postal code {string}")
    public void i_checkout(String firstName, String lastName, String postalCode) {
        driver.findElement(By.id("checkout")).click();
        driver.findElement(By.id("first-name")).sendKeys(firstName);
        driver.findElement(By.id("last-name")).sendKeys(lastName);
        driver.findElement(By.id("postal-code")).sendKeys(postalCode);
        driver.findElement(By.id("continue")).click();
        driver.findElement(By.id("finish")).click();
    }

    @Then("I should see the order confirmation message")
    public void i_should_see_confirmation() {
        String text = driver.findElement(By.className("complete-header")).getText();
        Assert.assertEquals(text, "Thank you for your order!");
    }

}
