package com.automationframework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;

import java.time.Duration;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;


public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(id = "user-name")
    private WebElement usernameField;
    @FindBy(id = "password")
    private WebElement passwordField;
    @FindBy(id = "login-button")
    private WebElement loginButton;
    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;
    @FindBy(css = "span.title")
    private WebElement productsTitle;


    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }
    public boolean isLoginButtonDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(loginButton)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameField)).sendKeys(username);
    }
    public void open() {
        String baseUrl = com.automationframework.util.ConfigReader.getUiProperty("baseUrl");
        driver.get(baseUrl);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordField)).sendKeys(password);
    }

    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }

    public String getErrorMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement error = wait.until(ExpectedConditions.visibilityOf(errorMessage));
        return error.getText();
    }

    public String getProductsTitle() {
        return wait.until(ExpectedConditions.visibilityOf(productsTitle)).getText();
    }
}

