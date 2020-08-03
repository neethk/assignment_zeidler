package com.zeidler.pages;

import com.zeidler.base.Environment;
import com.zeidler.base.TestUtilities;
import org.aeonbits.owner.ConfigFactory;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;


public class HomePage extends BasePageObject {
    Environment testEnvironment = ConfigFactory.create(Environment.class);

    private By addComputer = By.id("add");
    private By filterText = By.id("searchbox");
    private By filterButton = By.id("searchsubmit");

    //Add computer locators
    private By computerName = By.id("name");
    private By introducedDate = By.id("introduced");
    private By discontinuedDate = By.id("discontinued");
    private By companyname = By.id("company");
    private By createComputer = By.xpath("//body/section[@id='main']/form/div/input[1]");
    private By sbanner = By.xpath("//section[@id='main']//div[1]");
    private By cancelButton = By.xpath("//a[contains(text(),'Cancel')]");
    private By successbanner = By.xpath("//section[@id='main']//div[1]");

    private By mandateFieldAlert=By.id("name");




    public HomePage(WebDriver driver, Logger log) {
        super(driver, log);
    }

    //Get total count of computers
    public String getTotalComputerCount() {
        WebElement headercount = driver.findElement(By.xpath("//section[@id='main']//h1"));
        String totalComputers = headercount.getText();
        int index = totalComputers.indexOf(' ');
        return totalComputers.substring(0, index).trim();
    }

    // create a new computer with all cases from addComputer.csv (src/test/resources/dataproviders/homepageTest/addComputer.csv)
    public String createNewComputer(String computername, String introduceddate, String discontinueddate, String Company) {
        String successMessage = null;
        try {
            driver.navigate().refresh();
            waitForVisibilityOf(addComputer, 10);
            click(addComputer);
            if (computername.equalsIgnoreCase("")) {
                click(createComputer);
                String expected_result =getTextFromElement(mandateFieldAlert);
                String actual_result = "Required";
                if (expected_result.equalsIgnoreCase(actual_result)) {
                    log.info("the computer name is " + expected_result);
                }
            } else {
                sendChar(computerName, computername);
            }
            if (introduceddate.equalsIgnoreCase("")) {
                log.info("No introduced date");
            } else {
                sendChar(introducedDate, introduceddate);
            }
            if (discontinueddate.equalsIgnoreCase("")) {
                log.info("No discontinued date");
            } else {
                sendChar(discontinuedDate, discontinueddate);
            }
            if (Company.equalsIgnoreCase("")) {
                log.info("No company name ");
            } else {
                click(companyname);
                Select companySelect = new Select(driver.findElement(By.id("company")));
                companySelect.selectByVisibleText(Company);
            }
            click(createComputer);
            visibilityCheck(sbanner);
            successMessage=getTextFromElement(successbanner);
            log.info("The banner exists" + successMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return successMessage;
    }




}

