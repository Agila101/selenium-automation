# Selenium Automation Framework 


## Overview
This project is a **Selenium Automation Framework** built for automating the **Swag Labs** web application. It demonstrates both **UI automation** and **BDD (Behavior-Driven Development)** practices, making it a great showcase for SDET roles and QA automation portfolios.

The framework includes **parallel testing**, **data-driven testing** using Excel, **cross-browser testing**, and **detailed test reports**.

---

## Features

- **UI Automation:** Automates core functionalities like login, product sorting, and checkout.
- **BDD with Cucumber:** Implements scenarios in `.feature` files for readable and maintainable tests.
- **Data-Driven Testing:** Reads test data from Excel sheets.
- **Cross-Browser Testing:** Supports running tests on Chrome and Firefox.
- **Parallel Execution:** Runs multiple tests concurrently to save time.
- **Detailed Reports:** Generates test reports for both BDD and UI tests.

---

## Tools & Technologies

- **Language:** Java
- **Testing Framework:** TestNG
- **Automation Tool:** Selenium WebDriver
- **BDD:** Cucumber
- **Data Handling:** Apache POI for Excel
- **Reporting:** Extent Reports / TestNG Reports
- **Build Tool:** Maven

---

## Project Structure

selenium-automation/
│
├─ src/
│ ├─ main/
│ │ └─ java/
│ │ └─ com/automationframework/
│ ├─ test/
│ ├─ java/
│ │ └─ stepdefinitions/
│ │ └─ runners/
│ └─ resources/
│ └─ features/
│ ├─ SwagLabsLogin.feature
│ ├─ SwagLabsCheckout.feature
│ └─ SwagLabsSorting.feature
│
├─ testdata/
│ └─ Users.xlsx
│
├─ pom.xml
└─ README.md

yaml
Copy code

---

## How to Run the Project

1. **Clone the repository**
```bash
git clone https://github.com/Agila101/selenium-automation.git
Navigate to the project folder


cd selenium-automation
Run all tests using Maven


mvn clean test
Run specific BDD scenarios


mvn test -Dcucumber.options="--tags @ui"
Test Reports
Reports will be generated in the test-output folder.

Cucumber reports for feature execution are available in target/cucumber-reports.

Key Feature Files
SwagLabsLogin.feature – Login functionality

SwagLabsCheckout.feature – Checkout workflow

SwagLabsSorting.feature – Product sorting validation

Author
Agila Ethiraj
GitHub Profile: https://github.com/Agila101/selenium-automation

