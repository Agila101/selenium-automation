package com.automationframework.Listeners;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.automationframework.base.BaseTest;
import com.automationframework.reports.ExtentManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        if (test.get() == null) {
            ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
            test.set(extentTest);
        }
        test.get().log(Status.INFO, "Test started: " + result.getMethod().getMethodName());
    }
    private void log(Status status, String message) {
        if (test.get() != null) {
            test.get().log(status, message);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (test.get() != null) {
            test.get().log(Status.FAIL, "Test Failed: " + result.getThrowable());

            try {
                // Take screenshot
                BaseTest base = (BaseTest) result.getInstance();
                String base64Screenshot = ((TakesScreenshot) base.getDriver())
                        .getScreenshotAs(OutputType.BASE64);
                test.get().addScreenCaptureFromBase64String(base64Screenshot, "Failed Screenshot");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP, "Test Skipped: " + result.getMethod().getMethodName());
    }

    @Override
    public void onStart(ITestContext context) {
        // Nothing
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}
