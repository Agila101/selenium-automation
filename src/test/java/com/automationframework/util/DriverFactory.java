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

        boolean isCI = "true".equals(System.getenv("CI"));
        switch (browser.toLowerCase()) {
            case "chrome":

            WebDriverManager.chromedriver().setup();
            if (isCI) {
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--window-size=1920,1080");
                webDriver = new ChromeDriver(chromeOptions);
            } else {
                webDriver = new ChromeDriver();
            }
            break;


            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                if (isCI) {
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("--headless");
                    firefoxOptions.addArguments("--width=1920");
                    firefoxOptions.addArguments("--height=1080");
                    webDriver = new FirefoxDriver(firefoxOptions);
                } else {
                    webDriver = new FirefoxDriver();
                }
                break;

            case "edge":
                // Use your custom Edge driver path
                System.setProperty("webdriver.edge.driver", "/usr/local/bin/msedgedriver");
                if (isCI) {
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.addArguments("--headless=new");
                    edgeOptions.addArguments("--window-size=1920,1080");
                    webDriver = new EdgeDriver(edgeOptions);
                } else {
                    webDriver = new EdgeDriver();
                }
                break;

            default:
                throw new IllegalArgumentException("Browser not supported: " + browser);
        }
        webDriver.manage().window().maximize();
        webDriver.manage().deleteAllCookies();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.set(webDriver);
        System.out.println("Browser launched: " + browser + (isCI ? " (headless)" : ""));
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