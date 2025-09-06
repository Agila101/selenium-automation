package com.automationframework.tests;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import io.github.bonigarcia.wdm.WebDriverManager;


public class FireFox {
    public static void main(String[] args) {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();
        options.setBinary("/Applications/Firefox.app/Contents/MacOS/firefox");
        options.addArguments("--headless=false");

        WebDriver driver = new FirefoxDriver(options);
        driver.get("https://www.saucedemo.com");
        System.out.println(driver.getTitle());
        driver.quit();
    }

}
