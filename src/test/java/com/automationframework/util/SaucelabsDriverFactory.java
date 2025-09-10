package com.automationframework.util;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.URL;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import java.net.MalformedURLException;
import org.openqa.selenium.edge.EdgeOptions;

public class SaucelabsDriverFactory {
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

        /**
         * Initialize a Sauce Labs Remote WebDriver
         *
         * @param browserName    "chrome" or "firefox"
         * @param platform       OS name: "Windows 11", "macOS 13", etc.
         * @param browserVersion "latest", "latest-1", etc.
         * @return WebDriver instance
         */
        public static WebDriver initDriver(String browserName, String platform, String browserVersion) {
            String username = System.getenv("SAUCE_USERNAME");
            String accessKey = System.getenv("SAUCE_ACCESS_KEY");

            if (username == null || accessKey == null) {
                throw new IllegalStateException("Please set SAUCE_USERNAME and SAUCE_ACCESS_KEY environment variables.");
            }

            String sauceUrl = "https://" + username + ":" + accessKey + "@ondemand.saucelabs.com:443/wd/hub";
            WebDriver webDriver = null;

            try {
                DesiredCapabilities caps = new DesiredCapabilities();
                caps.setCapability("platformName", platform);
                caps.setCapability("browserVersion", browserVersion);
                caps.setCapability("name", "UI TDD Test"); // Optional: test name in Sauce Labs

                switch (browserName.toLowerCase()) {
                    case "edge":
                        EdgeOptions edgeOptions = new EdgeOptions();
                        edgeOptions.merge(caps);
                        webDriver = new RemoteWebDriver(new URL(sauceUrl), edgeOptions);
                        break;

                    case "firefox":
                        FirefoxOptions firefoxOptions = new FirefoxOptions();
                        firefoxOptions.merge(caps);
                        webDriver = new RemoteWebDriver(new URL(sauceUrl), firefoxOptions);
                        break;

                    default:
                        throw new IllegalArgumentException("Unsupported browser: " + browserName);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException("Invalid Sauce Labs URL");
            }

            driver.set(webDriver);
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

