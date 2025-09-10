package com.automationframework.util;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelUtil {
    private Workbook workbook;

    // Constructor loads the Excel file
    public ExcelUtil(String fileName) {
        try (FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/src/test/resources/testdata/" + fileName)) {
            workbook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load Excel file: " + fileName);
        }
    }

    // Get data from a specific cell
    public String getCellData(String sheetName, int rowNum, int colNum) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new RuntimeException("Sheet not found: " + sheetName);
        }
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            throw new RuntimeException("Row not found: " + rowNum);
        }
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            return "";
        }

        cell.setCellType(CellType.STRING); // Force as String for simplicity
        return cell.getStringCellValue();
    }

    // Optional: Close workbook after usage
    public void closeWorkbook() {
        try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getRowCount(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new RuntimeException("Sheet not found: " + sheetName);
        }
        return sheet.getLastRowNum() + 1; // +1 because row index starts at 0
    }

    public Sheet getSheet(String sheetName) {
        return workbook.getSheet(sheetName);
    }

    public List<Map<String, String>> getDataAsListOfMaps(String sheetName) {
        List<Map<String, String>> sheetData = new ArrayList<>();
        Sheet sheet = getSheet(sheetName);

        if (sheet == null) {
            throw new RuntimeException("Sheet not found: " + sheetName);
        }

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new RuntimeException("Header row not found in sheet: " + sheetName);
        }

        int colCount = headerRow.getLastCellNum();
        int rowCount = getRowCount(sheetName);

        for (int i = 1; i < rowCount; i++) { // Start from 1 to skip header
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Map<String, String> rowMap = new HashMap<>();
            for (int j = 0; j < colCount; j++) {
                Cell cell = row.getCell(j);
                String header = headerRow.getCell(j).getStringCellValue();
                String value = (cell != null) ? cell.toString() : "";
                rowMap.put(header, value);
            }
            sheetData.add(rowMap);
        }
        return sheetData;
    }
}


