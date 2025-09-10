package com.automationframework.base;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import com.automationframework.util.ConfigReader;
import com.automationframework.util.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;



public class ApiBaseTest {
    protected WebDriver driver;
    protected static ExtentReports extent;
    protected ExtentTest test;

    // Initialize ExtentReports once per suite
    @BeforeSuite(alwaysRun = true)
    public void setupExtent() {
        ExtentSparkReporter spark = new ExtentSparkReporter("ExtentReports/ApiUiExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownExtent() {
        extent.flush();
    }

    // Getter for WebDriver
    public WebDriver getDriver() {
        return driver;
    }

    // Only initialize WebDriver for tests that need UI
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser","env"})
    public void setUp(@Optional String browser, @Optional String environment) {
        if (browser == null) {
            browser = "firefox"; // default browser
        }
        if (environment != null) {
            ConfigReader.setEnv(environment); // switch environment if needed
        }

        // Initialize WebDriver only if driver is required
        driver = DriverFactory.initDriver(browser);

        // Navigate to base URL
        String baseUrl = ConfigReader.getProperty("api","baseUrl");
        driver.get(baseUrl);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            DriverFactory.quitDriver();
        }
    }
}
