package com.automationframework.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;

public class HomePage {

    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(className = "title")
    private WebElement title;

    @FindBy(css = ".inventory_item_name")
    private List<WebElement> productNames;

    @FindBy(className = "inventory_item")
    private List<WebElement> productItems;

    @FindBy(id = "change-password-modal") // adjust to actual modal ID
    private WebElement changePasswordModal;

    @FindBy(css = "#change-password-modal button.close")
    private WebElement closeModalButton;
    @FindBy(className = "inventory_item_price")
    private List<WebElement> productPrices;
    private By burgerMenuButton = By.id("react-burger-menu-btn");
    private By logoutLink = By.id("logout_sidebar_link");
    private By sortDropdown = By.cssSelector("select.product_sort_container");




    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    public boolean isProductListDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(productItems));
            return !productItems.isEmpty() && productItems.get(0).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }
    public void sortProducts(String sortOption) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(sortDropdown));
        Select select = new Select(dropdown);
        select.selectByVisibleText(sortOption);
    }
    public void clickProductByName(String productName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // increase timeout
        try {
            WebElement product = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class,'inventory_item_name') and normalize-space(text())='" + productName + "']"))
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", product);
            product.click();
        } catch (TimeoutException e) {
            throw new NoSuchElementException("Product not found on page: " + productName);
        }
    }

    public void logout() {
        try {
            WebElement menuButton = wait.until(ExpectedConditions.elementToBeClickable(burgerMenuButton));
            menuButton.click();

            WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(logoutLink));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", logoutBtn);
            logoutBtn.click();
        } catch (Exception e) {
            throw new RuntimeException("Logout failed: " + e.getMessage(), e);
        }
    }

    public void clickFirstProduct() {
        handleAlertAndModal();

        try {
            if (!productNames.isEmpty()) {
                WebElement firstProduct = productNames.get(0);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", firstProduct);
                wait.until(ExpectedConditions.elementToBeClickable(firstProduct)).click();
            } else {
                throw new RuntimeException("No products found on home page");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to click first product: " + e.getMessage(), e);
        }
    }

    private void handleAlertAndModal() {
        // Accept any browser alert
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            shortWait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            System.out.println("Alert accepted");
        } catch (Exception ignored) {}

        // Close Change Password modal if present
        try {
            if (changePasswordModal.isDisplayed()) {
                wait.until(ExpectedConditions.elementToBeClickable(closeModalButton)).click();
                System.out.println("Change Password modal dismissed");
            }
        } catch (Exception ignored) {}
    }
    public boolean selectProductByTitle(String title) {
        List<WebElement> products = wait.until(ExpectedConditions.visibilityOfAllElements(productNames));
        String normalizedTitle = title.toLowerCase().split(",")[0].trim();

        for (WebElement product : products) {
            String productText = product.getText().toLowerCase().trim();
            System.out.println("UI Product: " + productText);
            if (productText.contains(normalizedTitle)) {
                product.click();
                return true;
            }
        }
        return false;
    }
    public List<String> getAllProductTitles() {
        return productNames.stream().map(WebElement::getText).toList();
    }

    public boolean isProductVisible(String productTitle) {
        for (WebElement product : productNames) {
            if (product.getText().trim().equalsIgnoreCase(productTitle.trim())) {
                return true;
            }
        }
        return false;
    }
    public double getProductPrice(String productTitle) {
        for (int i = 0; i < productNames.size(); i++) {
            String title = productNames.get(i).getText().trim();
            if (title.equalsIgnoreCase(productTitle.trim())) {
                String priceText = productPrices.get(i).getText().replace("$", "").trim();
                return Double.parseDouble(priceText);
            }
        }
        throw new RuntimeException("Product not found: " + productTitle);
    }
    public void addProductToCartByIndex(int index) {
        List<WebElement> products = productItems;
        if (index < products.size()) {
            WebElement addBtn = products.get(index).findElement(By.cssSelector("button.btn_primary"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addBtn);
            addBtn.click();
        } else {
            throw new RuntimeException("Product index out of bounds");
        }
    }
}
