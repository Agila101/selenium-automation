package com.automationframework.util;
import org.testng.annotations.DataProvider;

public class ApiDataProviders {
    @DataProvider(name = "invalidProductPayloads")
    public static Object[][] invalidProductPayloads() {
        return new Object[][]{
                {"{\"title\":\"Product Without Price\"}", "Missing price"},
                {"{\"price\":29.99}", "Missing title"},
                {"{}", "Empty payload"},
                {"{\"title\":\"Invalid JSON\", \"price\":19.99", "Malformed JSON"}
        };
    }

    @DataProvider(name = "validProductUpdates")
    public static Object[][] validProductUpdates() {
        return new Object[][]{
                {"Updated Product 1", 49.99},
                {"Updated Product 2", 79.99},
                {"Updated Product 3", 19.99}
        };
    }

    @DataProvider(name = "priceBoundaryData")
    public static Object[][] getPriceData() {
        return new Object[][]{
                {0.0, false},       // below minimum
                {0.99, true},       // minimum valid
                {1000.0, true},     // maximum valid
                {1000.01, false},   // above maximum
                {-5, false}         // negative price
        };
    }

    @DataProvider(name="UserRoles")
    public Object[][] rolesData() {
        return new Object[][] {
                {"admin", "ADMIN_TOKEN", 200},
                {"user", "USER_TOKEN", 403},
                {"guest", "GUEST_TOKEN", 401}
        };
    }
    @DataProvider(name = "invalidProductIds")
    public Object[][] invalidProductIds() {
        return new Object[][] {
                {"nonexistent-id-1"},
                {"123-invalid"},
                {"!@#$%^&*()"}
        };
    }
}
