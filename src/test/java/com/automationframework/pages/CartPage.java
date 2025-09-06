package com.automationframework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.ElementClickInterceptedException;
import java.util.List;
import org.openqa.selenium.TimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.NoSuchElementException;

public class CartPage {
    private static final Logger logger = LogManager.getLogger(CartPage.class);

    private WebDriver driver;
    private WebDriverWait wait;

    private By shoppingCartIcon = By.cssSelector("a.shopping_cart_link");
    private By checkoutButton = By.cssSelector("button#checkout");
    private By cartItems = By.cssSelector(".cart_item");
    private By removeButtons = By.cssSelector("button.cart_button"); // adjust if needed
    private By addToCartButtons = By.cssSelector("button.btn_primary"); // if needed


    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }
    public int getCartItemsCount() {
        List<WebElement> items = driver.findElements(cartItems);
        return items.size();
    }
    public boolean isProductInCart(String productName) {
        List<WebElement> cartItems = driver.findElements(By.cssSelector(".inventory_item_name"));
        for (WebElement item : cartItems) {
            if (item.getText().trim().equals(productName)) {
                return true;
            }
        }
        return false;
    }

    public void removeProductByIndex(int index) {
        List<WebElement> buttons = driver.findElements(removeButtons);
        if (index < buttons.size()) {
            buttons.get(index).click();
        } else {
            throw new RuntimeException("Remove button index out of bounds");
        }
    }

        public void clickCartIcon() {
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
        }

        // Click on checkout button
        public void clickCheckout(){
            logger.info("Clicking on Checkout button");
            WebElement checkoutBtn = wait.until(ExpectedConditions.elementToBeClickable(checkoutButton));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", checkoutBtn);

            try {
                checkoutBtn.click();
            } catch (ElementClickInterceptedException e) {
                try {
                    Thread.sleep(500); // wait half a second before retrying
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                checkoutBtn.click();
            }
        }
    }


