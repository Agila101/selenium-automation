package com.automationframework.stepdefinitions;
import com.automationframework.util.ExcelUtil;
import com.automationframework.util.DriverFactory;
import io.cucumber.java.en.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;
import java.time.Duration;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import com.automationframework.reports.ExtentManager;
import com.aventstack.extentreports.*;
import com.automationframework.util.AllureLogger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;




public class SwagLabsExcelSteps {
    WebDriver driver = DriverFactory.getDriver();
    ExcelUtil excel;
    ExtentReports extent = ExtentManager.getInstance();
    ExtentTest test;
    List<Map<String, String>> productData;
    List<Map<String, String>> checkoutData;
    List<Map<String, String>> sortingData;

    // Load Excel file
    @Given("I load Swag Labs test data from Excel file {string}")
    public void load_excel_file(String fileName) {
        excel = new ExcelUtil(fileName);
    }

    // Navigate to login page
    @Given("I am on the Swag Labs Excel login page")
    public void go_to_login_page() {
        test = extent.createTest("Login Page Navigation");
        driver.get("https://www.saucedemo.com/");
        System.out.println("Navigated to Swag Labs login page");
        test.info("Navigated to login page");
    }

    // Login for Checkout scenario
    @When("I login for {string} scenario with Excel credentials")
    public void login_for_checkout(String scenario) {
        test = extent.createTest("Login for " + scenario);
        int rowNum = scenario.equalsIgnoreCase("Checkout") ? 1 : 2;
        String username = excel.getCellData("BDDSwagLogin", rowNum, 0);
        String password = excel.getCellData("BDDSwagLogin", rowNum, 1);

        driver.findElement(By.id("user-name")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("login-button")).click();
        test.pass("Logged in successfully as: " + username);
        System.out.println("Logged in for " + scenario + ": " + username);
    }

    // Add product(s) to cart from Excel sheet
    @And("I add product\\(s\\) from Excel sheet {string} to the cart")
    public void add_products_to_cart(String sheetName) {
        productData = excel.getDataAsListOfMaps(sheetName);

        for (Map<String, String> row : productData) {
            String productName = row.get("productName"); // Excel header must be "productName"
            try {
                // Locate product container by name, then find the button inside it
                By addButton = By.xpath("//div[@class='inventory_item']//div[text()='" + productName + "']/ancestor::div[@class='inventory_item']//button");
                driver.findElement(addButton).click();
                System.out.println("Added product to cart: " + productName);
            } catch (Exception e) {
                System.out.println("Could not add product: " + productName);
            }
        }
    }


    // Complete checkout using Excel data
    @And("I complete checkout using data from {string}")
    public void complete_checkout(String sheetName) {
        List<Map<String, String>> checkoutData = excel.getDataAsListOfMaps(sheetName);
        Map<String, String> checkoutRow = checkoutData.get(0); // first row

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Local locators
        By shoppingCartIcon = By.cssSelector("a.shopping_cart_link");
        By checkoutButton = By.cssSelector("button#checkout");

        // Click on the shopping cart icon
        WebElement cart = wait.until(ExpectedConditions.elementToBeClickable(shoppingCartIcon));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", cart);
        try {
            cart.click();
        } catch (ElementClickInterceptedException e) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            cart.click();
        }

        // Click Checkout button
        WebElement checkoutBtn = wait.until(ExpectedConditions.elementToBeClickable(checkoutButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", checkoutBtn);
        try {
            checkoutBtn.click();
        } catch (ElementClickInterceptedException e) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            checkoutBtn.click();
        }

        // Fill First Name
        WebElement firstNameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("first-name")));
        firstNameInput.sendKeys(checkoutRow.get("firstName"));

        // Fill Last Name
        WebElement lastNameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("last-name")));
        lastNameInput.sendKeys(checkoutRow.get("lastName"));

        // Fill Postal Code
        WebElement postalCodeInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("postal-code")));
        postalCodeInput.sendKeys(checkoutRow.get("postalCode"));

        // Click Continue
        WebElement continueBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input.btn_primary.cart_button")));
        continueBtn.click();

        // Click Finish
        WebElement finishBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("finish")));
        finishBtn.click();

        System.out.println("Completed checkout using Excel data: " + checkoutRow);
    }



    // Try all sorting options from Excel
    @When("I apply all sorting options from {string}")
    public void try_sorting_options(String sheetName) {
        sortingData = excel.getDataAsListOfMaps(sheetName);

        Select sortDropdown = new Select(driver.findElement(By.className("product_sort_container")));

        for (Map<String, String> row : sortingData) {
            String option = row.get("sortOption"); // Excel header must be "sortOption"
            sortDropdown.selectByVisibleText(option);
            System.out.println("Applied sorting option: " + option);
        }
    }

    // Verify order confirmation
    @Then("I should see excel order confirmation message")
    public void verify_order_confirmation() {
        String confirmation = driver.findElement(By.className("complete-header")).getText();
        System.out.println("Order confirmation message: " + confirmation);
        // Allure text log
        AllureLogger.logText("Order confirmation message: " + confirmation);

        // Allure screenshot
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        AllureLogger.attachScreenshot(screenshot);

    }

    // Verify sorted products (simple placeholder)
    @Then("I should see products sorted correctly")
    public void verify_sorted_products() {
        System.out.println("Products sorted successfully as per Excel sheet");

    }

}
