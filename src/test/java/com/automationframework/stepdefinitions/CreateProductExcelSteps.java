package com.automationframework.stepdefinitions;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import com.automationframework.util.ExcelUtil;
import java.util.*;
import java.util.HashMap;
import java.util.Map;
import io.cucumber.messages.types.Product;



public class CreateProductExcelSteps {
    private List<Map<String, Object>> payloads = new ArrayList<>();
    private List<Response> responses = new ArrayList<>();
    private Hooks hooks = new Hooks(); // use hooks to log API responses

    @Given("I prepare the product payload from Excel file {string} and sheet {string}")
    public void prepareProductPayloadFromExcel(String fileName, String sheetName) {
        ExcelUtil excel = new ExcelUtil(fileName);
        int rowCount = excel.getRowCount(sheetName);

        for (int i = 1; i < rowCount; i++) { // skip header
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", excel.getCellData(sheetName, i, 0));
            payload.put("price", Double.parseDouble(excel.getCellData(sheetName, i, 1)));
            payload.put("description", excel.getCellData(sheetName, i, 2));
            payload.put("category", excel.getCellData(sheetName, i, 3));
            payload.put("image", excel.getCellData(sheetName, i, 4));
            payloads.add(payload);
        }
        excel.closeWorkbook();
    }

    @When("I send POST requests for all products")
    public void sendPostRequestsForAllProducts() {
        for (Map<String, Object> payload : payloads) {
            Response response = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(payload)
                    .post()
                    .then().extract().response();

            responses.add(response);
            hooks.setLastApiResponse(response); // store last response for hooks reporting

            System.out.println("POST Response: " + response.getBody().asString());
        }
    }

    @Then("each product response status code should be {int} and match the payload")
    public void validateResponses(int expectedStatusCode) {
        for (int i = 0; i < responses.size(); i++) {
            Response response = responses.get(i);
            Map<String, Object> expectedProduct = payloads.get(i);

            // Status code check
            Assert.assertEquals(response.getStatusCode(), expectedStatusCode,
                    "Status code mismatch for product " + (i + 1));

            // Price comparison with delta
            double expectedPrice = (Double) expectedProduct.get("price");
            double actualPrice = response.jsonPath().getDouble("price");
            double delta = 0.01;
            if (Math.abs(actualPrice - expectedPrice) > delta) {
                Assert.fail("Price mismatch for product " + (i + 1) + ": expected ["
                        + expectedPrice + "] but found [" + actualPrice + "]");
            }

            // Other fields
            Assert.assertEquals(response.jsonPath().getString("title"), expectedProduct.get("title"),
                    "Title mismatch for product " + (i + 1));
            Assert.assertEquals(response.jsonPath().getString("description"), expectedProduct.get("description"),
                    "Description mismatch for product " + (i + 1));
            Assert.assertEquals(response.jsonPath().getString("category"), expectedProduct.get("category"),
                    "Category mismatch for product " + (i + 1));
            Assert.assertEquals(response.jsonPath().getString("image"), expectedProduct.get("image"),
                    "Image URL mismatch for product " + (i + 1));
        }
    }
}


