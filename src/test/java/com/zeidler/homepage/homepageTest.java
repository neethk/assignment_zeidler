package com.zeidler.homepage;
import com.zeidler.base.CsvDataProviders;
import com.zeidler.base.Environment;
import com.zeidler.base.SoftAssertion;
import com.zeidler.base.TestUtilities;
import com.zeidler.pages.BasePageObject;
import com.zeidler.pages.HomePage;
import org.aeonbits.owner.ConfigFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.Map;


public class homepageTest extends TestUtilities {
    public String environment;
    Environment testEnvironment;
    SoftAssert softAssert = null;

    @BeforeMethod(alwaysRun = true)
    @Parameters({"environment"})
    public void beforeMethod(String environment) {
        try {
            log.info("Starting Test.");
            this.environment = environment;
            log.info("The env value is: " + environment);
            ConfigFactory.setProperty("env", environment);
            testEnvironment = ConfigFactory.create(Environment.class);
            driver.get(testEnvironment.url());
            HomePage homePage = new HomePage(driver, log);
        } catch (Exception e) {
            TestUtilities.printTraceLog(e);
        }
    }


    /*actual test which picks up data from the csv
    and executes 6 tests one after the other
    for all validations in Add comouter */

    @Test(dataProvider = "csvReader", dataProviderClass = CsvDataProviders.class, priority = 0)
    @Parameters({"environment"})

    public void addComputer(Map<String, String> testData) throws Exception{
        softAssert = new SoftAssert();
        String no = testData.get("no");
        log.info(no);
        int iNummber = Integer.parseInt(no);
        String computername = testData.get("computername");
        String introduceddate = testData.get("introduceddate");
        String discontinueddate = testData.get("discontinueddate");
        String Company = testData.get("Company");

        log.info("**** Started : add computers ***");
        softAssert = new SoftAssert();
        HomePage homePage = new HomePage(driver, log);
        String count = homePage.getTotalComputerCount();
        log.info("total number of computers in the system" + count);
        String SuccessCreation= homePage.createNewComputer( computername, introduceddate, discontinueddate, Company);
        String expectedresult = "Done! Computer " + computername + " has been created";
        Assert.assertEquals(SuccessCreation,expectedresult);
        softAssert.assertAll();

    }

    /* would have created edit computer and search computer testcases if i had some more time */
    /*@Test
    public void editComputer() {


    }
    @Test
    public void searchComputer() {

    }*/


    /*@AfterMethod()
    public void closeTab() {
        log.info("Close Tabs");
        driver.close();
    }*/
}
