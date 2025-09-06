package com.automationframework.util;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public class DriverFactory {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver initDriver(String browser) {
        WebDriver webDriver;

        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                webDriver  = new ChromeDriver();
                break;

            case "chrome-headless":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--window-size=1920,1080");
                webDriver = new ChromeDriver(chromeOptions);
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                webDriver = new FirefoxDriver();
                break;

            case "firefox-headless":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--headless");
                firefoxOptions.addArguments("--width=1920");
                firefoxOptions.addArguments("--height=1080");
                webDriver = new FirefoxDriver(firefoxOptions);
                break;

            case "edge":
                System.setProperty("webdriver.edge.driver", "/usr/local/bin/msedgedriver");
                webDriver = new EdgeDriver();
                break;

            case "edge-headless":
                System.setProperty("webdriver.edge.driver", "/usr/local/bin/msedgedriver");
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--headless=new");
                edgeOptions.addArguments("--window-size=1920,1080");
                webDriver = new EdgeDriver(edgeOptions);
                break;

            default:
                throw new IllegalArgumentException("Browser not supported: " + browser);
        }

        webDriver.manage().window().maximize();
        webDriver.manage().deleteAllCookies();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.set(webDriver);
        System.out.println("Browser launched: " + browser);
        return getDriver();
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}