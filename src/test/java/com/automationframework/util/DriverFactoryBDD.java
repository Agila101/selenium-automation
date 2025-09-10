package com.automationframework.util;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactoryBDD {
    // Thread-safe WebDriver for parallel execution
    private static ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();

    public static WebDriver createDriver(String browser) {
        WebDriver driver;

        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            default:
                throw new RuntimeException("Unsupported browser: " + browser);
        }

        driverThread.set(driver);
        return driver;
    }

    public static WebDriver getDriver() {
        return driverThread.get();
    }

    public static void removeDriver() {
        driverThread.remove();
    }
}

