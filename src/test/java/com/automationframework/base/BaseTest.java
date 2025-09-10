package com.automationframework.base;

import com.automationframework.util.ConfigReader;
import com.automationframework.util.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class BaseTest {

    protected WebDriver driver;
    protected static ExtentReports extent;
    protected ExtentTest test;

    @BeforeSuite(alwaysRun = true)
    public void setupExtent() {
        ExtentSparkReporter spark = new ExtentSparkReporter("ExtentReports/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    // Flush ExtentReports after all tests
    @AfterSuite(alwaysRun = true)
    public void tearDownExtent() {
        extent.flush();
    }

    public WebDriver getDriver() {
        return driver;
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser","env"})
    public void setUp(@Optional String browser, @Optional String environment) {
        if (browser == null) {
            browser = "firefox"; // get default browser from config
        }
        if (environment != null) {
            ConfigReader.setEnv(environment); // switch environment
        }
        boolean isCI = "true".equals(System.getenv("CI"));
        if (isCI) {
            System.out.println("Running in CI environment, using headless browser if applicable.");
        }
        driver = DriverFactory.initDriver(browser);
        String baseUrl = ConfigReader.getProperty("ui","baseUrl");
        driver.get(baseUrl);


    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}