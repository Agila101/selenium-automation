package com.automationframework.util;
import com.automationframework.util.ExcelUtil;
import org.testng.annotations.DataProvider;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;


public class TestDataProviders {
    @DataProvider(name = "userData")
    public static Object[][] getUserData() {
        ExcelUtil excel = null;
        Object[][] data = null;
        try {
            excel = new ExcelUtil("Users.xlsx");
            String sheetName = "Users";
            int rowCount = excel.getRowCount(sheetName);
            int colCount = 2;
            data = new Object[rowCount - 1][colCount];
            for (int i = 1; i < rowCount; i++) {
                data[i - 1][0] = excel.getCellData(sheetName, i, 0);
                data[i - 1][1] = excel.getCellData(sheetName, i, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (excel != null) excel.closeWorkbook();
        }
        return data;
    }

    @DataProvider(name = "loginData")
    public static Object[][] loginData() {
        ExcelUtil excel = new ExcelUtil("Users.xlsx");
        String sheetName = "Usy";
        int rowCount = excel.getRowCount(sheetName);
        Object[][] data = new Object[rowCount - 1][4];

        for (int i = 1; i < rowCount; i++) {
            data[i - 1][0] = excel.getCellData(sheetName, i, 0); // username
            data[i - 1][1] = excel.getCellData(sheetName, i, 1); // password
            data[i - 1][2] = excel.getCellData(sheetName, i, 2); // type
            data[i - 1][3] = excel.getCellData(sheetName, i, 3); // expected error
        }
        excel.closeWorkbook();
        return data;
    }

    @DataProvider(name = "checkoutData")
    public static Object[][] getCheckoutData() {
        ExcelUtil excel = new ExcelUtil("Users.xlsx");
        String sheetName = "Users";
        int rowCount = excel.getRowCount(sheetName);
        int colCount = 5;

        Object[][] data = new Object[rowCount - 1][colCount];
        for (int i = 1; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                data[i - 1][j] = excel.getCellData(sheetName, i, j);
            }
        }
        excel.closeWorkbook();
        return data;
    }





    @DataProvider(name = "userDataTwo")
    public static Object[][] getUserDataTwo() {
        ExcelUtil excel = new ExcelUtil("Users.xlsx");
        String sheetName = "Users";
        int rowCount = excel.getRowCount(sheetName);
        Object[][] data = new Object[rowCount - 1][2];
        for (int i = 1; i < rowCount; i++) {
            data[i - 1][0] = excel.getCellData(sheetName, i, 0); // username
            data[i - 1][1] = excel.getCellData(sheetName, i, 1); // password
        }
        excel.closeWorkbook();
        return data;
    }

    @DataProvider(name = "excelUserProductData")
    public Object[][] getProductData() {
        ExcelUtil excel = new ExcelUtil("Users.xlsx");
        String sheetName = "ProductTitle";
        int rowCount = excel.getRowCount(sheetName);

        // List of products to test
        Object[][] data = new Object[rowCount - 1][6]; // 6 columns now (username, password, firstName, lastName, zip, productTitle)

        for (int i = 1; i < rowCount; i++) {
            data[i - 1][0] = excel.getCellData("ProductTitle", i, 0); // username
            data[i - 1][1] = excel.getCellData("ProductTitle", i, 1); // password
            data[i - 1][2] = excel.getCellData("ProductTitle", i, 2); // firstName
            data[i - 1][3] = excel.getCellData("ProductTitle", i, 3); // lastName
            data[i - 1][4] = excel.getCellData("ProductTitle", i, 4); // zip
            data[i - 1][5] = excel.getCellData("ProductTitle", i, 5); // productTitle
        }

        excel.closeWorkbook();
        return data;
    }
    @DataProvider(name = "excelUserProductDataEdge")
    public Object[][] getProductDataTwo() {
        ExcelUtil excel = new ExcelUtil("Users.xlsx");
        String sheetName = "EdgeCase";
        int rowCount = excel.getRowCount(sheetName);

        // List of products to test
        Object[][] data = new Object[rowCount - 1][6]; // 6 columns now (username, password, firstName, lastName, zip, productTitle)

        for (int i = 1; i < rowCount; i++) {
            data[i - 1][0] = excel.getCellData("EdgeCase", i, 0); // username
            data[i - 1][1] = excel.getCellData("EdgeCase", i, 1); // password
            data[i - 1][2] = excel.getCellData("EdgeCase", i, 2); // firstName
            data[i - 1][3] = excel.getCellData("EdgeCase", i, 3); // lastName
            data[i - 1][4] = excel.getCellData("EdgeCase", i, 4); // zip
            data[i - 1][5] = excel.getCellData("EdgeCase", i, 5); // productTitle
        }

        excel.closeWorkbook();
        return data;
    }
    @DataProvider(name = "usersWithProducts")
    public Object[][] getUsersWithProducts() {
        ExcelUtil excel = new ExcelUtil("Users.xlsx");
        String sheetName = "Limited"; // your sheet name
        int rowCount = excel.getRowCount(sheetName);

        // Group products by username
        Map<String, List<String>> userProducts = new HashMap<>();
        Map<String, String> userPasswords = new HashMap<>();

        for (int i = 1; i < rowCount; i++) {
            String username = excel.getCellData(sheetName, i, 0);// username
            String password = excel.getCellData(sheetName, i, 1);//password
            String productTitle = excel.getCellData(sheetName, i, 5); // productTitle
            userProducts.computeIfAbsent(username, k -> new ArrayList<>()).add(productTitle);
            userPasswords.putIfAbsent(username, password);
        }

        Object[][] data = new Object[userProducts.size()][3];
        int index = 0;
        for (String username : userProducts.keySet()) {
            data[index][0] = username;
            data[index][1] = userPasswords.get(username);
            data[index][2] = userProducts.get(username);
            index++;
        }

        excel.closeWorkbook();
        return data;
    }
}
