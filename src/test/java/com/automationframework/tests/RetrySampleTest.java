package com.automationframework.tests;


import com.automationframework.base.BaseTest;
import com.automationframework.Listeners.RetryAnalyzer;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.automationframework.Listeners.TestListener;
@Listeners(com.automationframework.Listeners.TestListener.class)
public class RetrySampleTest extends BaseTest {
    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void sampleRetryTest() {
        System.out.println("Executing sampleRetryTest...");
        // This test is intentionally failing to verify retry logic
        Assert.assertTrue(false, "Failing test to check retry logic");
    }

}


