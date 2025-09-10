package com.automationframework.util;
import io.qameta.allure.Attachment;

public class AllureLogger {
    @Attachment(value = "{0}", type = "text/plain")
    public static String logText(String message) {
        return message;
    }

    @Attachment(value = "Screenshot", type = "image/png")
    public static byte[] attachScreenshot(byte[] screenshot) {
        return screenshot;
    }
}
