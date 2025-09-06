package com.automationframework.tests;

import com.automationframework.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.automationframework.Listeners.TestListener;

@Listeners(TestListener.class)
public class DemoFailingTest extends BaseTest {

    @Test
    public void failingTestPage() {
        // Step 1: Open the website
        driver.get("https://www.saucedemo.com");

        // Step 2: Perform an assertion that will fail
        String expectedTitle = "This Title is Wrong"; // deliberately wrong
        String actualTitle = driver.getTitle();

        // This will fail and trigger the listener to take a screenshot
        Assert.assertEquals(actualTitle, expectedTitle, "Page title does not match!");
    }
}
