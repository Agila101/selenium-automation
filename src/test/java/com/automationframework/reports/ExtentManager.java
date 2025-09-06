package com.automationframework.reports;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {
    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        if (extent == null) {
            createInstance("test-output/ExtentReport.html");
        }
        return extent;
    }

    public static ExtentReports createInstance(String fileName) {
        ExtentSparkReporter reporter = new ExtentSparkReporter(fileName);
        reporter.config().setDocumentTitle("Automation Report");
        reporter.config().setReportName("Functional Test Report");

        extent = new ExtentReports();
        extent.attachReporter(reporter);
        extent.setSystemInfo("Tester", "Agila");
        extent.setSystemInfo("Environment", "QA");

        return extent;
    }
}

