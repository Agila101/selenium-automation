package com.automationframework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

public class CheckoutPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final Logger logger = LogManager.getLogger(CheckoutPage.class);

    private By firstNameField = By.id("first-name");
    private By lastNameField = By.id("last-name");
    private By postalCodeField = By.id("postal-code");
    private By continueBtn = By.cssSelector("input.btn_primary.cart_button");
    private By finishBtn = By.id("finish");
    private By confirmationMsg = By.cssSelector(".complete-header");
    private By checkoutStepTwoIndicator = By.cssSelector(".checkout_info");
    private By errorMessage = By.cssSelector("h3[data-test='error']");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        logger.info("CheckoutPage initialized");
    }
    private void waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameField));
        logger.info("Checkout page loaded");
    }

    public void enterCheckoutInfo(String firstName, String lastName, String postalCode) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameField)).sendKeys(firstName);
        driver.findElement(lastNameField).sendKeys(lastName);
        driver.findElement(postalCodeField).sendKeys(postalCode);
        logger.info("Entered checkout info: FirstName='{}', LastName='{}', PostalCode='{}'", firstName, lastName, postalCode);
    }

    public void clickContinue() {
        wait.until(ExpectedConditions.elementToBeClickable(continueBtn)).click();
        // Wait for either step 2 page or Finish button to appear
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(checkoutStepTwoIndicator),
                ExpectedConditions.visibilityOfElementLocated(finishBtn)
        ));
        logger.info("Clicked Continue button and proceeded to next step");
    }

    public void clickFinish() {
        // Wait until Finish button is clickable
         wait.until(ExpectedConditions.elementToBeClickable(finishBtn)).click();
        logger.info("Clicked Finish button to complete checkout");
    }
    public String getErrorMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
        } catch (TimeoutException e) {
            return "";
        }
    }


    public String getConfirmationMessage() {
        String msg=wait.until(ExpectedConditions.visibilityOfElementLocated(confirmationMsg)).getText();
        logger.info("Checkout confirmation message displayed: '{}'", msg);
        return msg;
    }
}