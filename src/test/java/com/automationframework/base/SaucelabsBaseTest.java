package com.automationframework.base;
import com.automationframework.util.SaucelabsDriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import com.automationframework.util.ConfigReader;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;


public class SaucelabsBaseTest {
    protected WebDriver driver;
    protected static ExtentReports extent;
    protected ExtentTest test;

    @BeforeSuite(alwaysRun = true)
    public void setupExtent() {
        ExtentSparkReporter spark = new ExtentSparkReporter("ExtentReports/SauceLabsExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Tester", "Agila");
        extent.setSystemInfo("Environment", "SauceLabs");
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownExtent() {
        extent.flush();
    }

    public WebDriver getDriver() {
        return driver;
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "platform", "browserVersion"})
    public void setUp(@Optional("chrome") String browser,
                      @Optional("Windows 11") String platform,
                      @Optional("latest") String browserVersion) {

        driver = SaucelabsDriverFactory.initDriver(browser, platform, browserVersion);

        String baseUrl = ConfigReader.getProperty("ui","baseUrl");
        driver.get(baseUrl);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        SaucelabsDriverFactory.quitDriver();
    }
}

