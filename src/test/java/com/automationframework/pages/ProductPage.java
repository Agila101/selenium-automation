package com.automationframework.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;

public class ProductPage {

    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(css = "button.btn_primary.btn_inventory")
    private WebElement addToCartButton;

    @FindBy(id = "back-to-products")
    private WebElement backButton;

    private By removeButtonLocator = By.cssSelector("button.btn_secondary.btn_inventory");
    private By productTitle = By.className("inventory_details_name");
    private By productPrice = By.className("inventory_details_price");
    private By productContainer = By.className("inventory_item");
    private By cartBadge = By.className("shopping_cart_badge");
    private By cartIcon = By.id("shopping_cart_container");
    private By cartItemPrices = By.cssSelector(".cart_item .inventory_item_price");
    private By productsTitleLocator = By.cssSelector("span.title");
    public ProductPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    public boolean isProductPageDisplayed() {
        try {
            WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(productsTitleLocator));
            return title.getText().equalsIgnoreCase("Products");
        } catch (TimeoutException e) {
            return false;
        }
    }
    public String getProductTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(productTitle)).getText().trim();
    }
    public void removeProductFromCartByName(String productName) {
        WebElement removeBtn = driver.findElement(By.xpath("//div[text()='" + productName + "']/ancestor::div[@class='inventory_item']//button[contains(text(),'Remove')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", removeBtn);
        removeBtn.click();
    }
    public double getProductPriceByName(String productName) {
        WebElement priceElement = driver.findElement(By.xpath("//div[text()='" + productName + "']/ancestor::div[@class='inventory_item']//div[@class='inventory_item_price']"));
        return Double.parseDouble(priceElement.getText().replace("$", "").trim());
    }

    // NEW: Get product price
    public double getProductPrice() {
        String priceText = wait.until(ExpectedConditions.visibilityOfElementLocated(productPrice)).getText().replace("$", "").trim();
        return Double.parseDouble(priceText);
    }

    public void addToCart() {
        try {
            if (addToCartButton.getText().equalsIgnoreCase("ADD TO CART")) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addToCartButton);
                addToCartButton.click();
                wait.until(ExpectedConditions.textToBePresentInElement(addToCartButton, "REMOVE"));
            }
        } catch (TimeoutException e) {
            System.err.println("Failed to add to cart: " + e.getMessage());
        }
    }

    public void removeFromCart() {
        try {
            WebElement removeBtn = wait.until(ExpectedConditions.elementToBeClickable(removeButtonLocator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", removeBtn);
            removeBtn.click();
            wait.until(ExpectedConditions.visibilityOf(addToCartButton));
        } catch (TimeoutException e) {
            System.err.println("Remove button not clickable");
        }
    }

    public boolean isRemoveButtonDisplayed() {
        try {
            WebElement removeBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(removeButtonLocator));
            return removeBtn.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isAddToCartButtonDisplayed() {
        try {
            return addToCartButton.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void goBack() {
        driver.navigate().back();
    }

public void addProductToCartByName(String productName) {
    List<WebElement> products = driver.findElements(productContainer);
    for (WebElement product : products) {
        String title = product.findElement(By.className("inventory_item_name")).getText();
        if (title.equalsIgnoreCase(productName)) {
            product.findElement(By.tagName("button")).click();
            break;
        }
    }
}

    public int getCartCount() {
        try {
            String countText = driver.findElement(cartBadge).getText();
            return Integer.parseInt(countText);
        } catch (NoSuchElementException e) {
            return 0;
        }
    }
    public double getCartTotalPrice() {
        List<WebElement> prices = driver.findElements(cartItemPrices);
        double total = 0.0;
        for (WebElement price : prices) {
            total += Double.parseDouble(price.getText().replace("$", "").trim());
        }
        return total;
    }

    public void goToCart() {
        driver.findElement(cartIcon).click();
    }


}
