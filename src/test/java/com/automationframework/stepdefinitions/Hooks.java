package com.automationframework.stepdefinitions;

import com.automationframework.util.DriverFactory;
import com.automationframework.reports.ExtentManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import io.cucumber.java.*;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.automationframework.util.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Hooks {
    private static ExtentReports extent = ExtentManager.getInstance();
    private static ExtentTest scenarioTest;
    private WebDriver driver;
    private Response lastApiResponse; // Store last API response for reporting

    @Before
    public void setUp(Scenario scenario) {
        scenarioTest = extent.createTest(scenario.getName());

        if (scenario.getSourceTagNames().contains("@ui")) {
            driver = DriverFactory.initDriver("firefox");
        } else if (scenario.getSourceTagNames().contains("@api")) {
            String baseUrl = ConfigReader.getApiProperty("baseUrl");
            if (baseUrl == null || baseUrl.isEmpty()) {
                baseUrl = "https://fakestoreapi.com"; // fallback
            }
            RestAssured.baseURI = baseUrl;
            System.out.println("API Base URI set to: " + RestAssured.baseURI);
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.getSourceTagNames().stream().anyMatch(tag -> tag.equalsIgnoreCase("@ui")) && driver != null) {
            if (scenario.isFailed()) {
                scenarioTest.fail("Scenario failed: " + scenario.getName());
                takeScreenshotForReports(scenario);
            }
            driver.quit();
            DriverFactory.setDriver(null);
            driver = null;
        }

        // API reporting
        if (scenario.getSourceTagNames().contains("@api") && lastApiResponse != null) {
            if (scenario.isFailed()) {
                scenarioTest.fail("API Scenario failed: " + scenario.getName());
                String responseBody = lastApiResponse.getBody().asString();
                Allure.addAttachment("Failed API Response", responseBody);
                scenarioTest.info("Failed API Response: " + responseBody);
            }
        }
    }

    private void takeScreenshotForReports(Scenario scenario) {
        try {
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String screenshotPath = "test-output/screenshots/" + scenario.getName().replaceAll(" ", "_") + ".png";
            Files.createDirectories(Paths.get("test-output/screenshots/"));
            Files.write(Paths.get(screenshotPath), screenshotBytes);
            scenarioTest.addScreenCaptureFromPath(screenshotPath);
            Allure.addAttachment("Failure Screenshot", new ByteArrayInputStream(screenshotBytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void afterAll() {
        extent.flush();
    }

    public static ExtentTest getScenarioTest() {
        return scenarioTest;
    }

    // Method to set last API response for reporting
    public void setLastApiResponse(Response response) {
        this.lastApiResponse = response;
    }
}
