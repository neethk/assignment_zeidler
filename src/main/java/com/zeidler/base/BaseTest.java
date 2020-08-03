package com.zeidler.base;

import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Listeners({com.zeidler.base.TestListener.class})
public class BaseTest {
    public static Logger log = new LoggerLog4j().initiateLogger();
    public static WebDriver driver;
    public static String env = null;
    public static String username1 = null;
    public static String password1 = null;
    protected String testSuiteName;
    protected String testName;
    protected String testMethodName;
    Environment testEnvironment;

    @Parameters({"chromeProfile", "deviceName"})
    @BeforeSuite(alwaysRun = true)
    public void setUp(Method method, @Optional String profile, @Optional String deviceName, ITestContext ctx) {
        try {
            env = System.getProperty("environment");
            String browser = System.getProperty("browser");
            String run = System.getProperty("run");
            System.out.println("The env value is : " + env);
            ConfigFactory.setProperty("env", BaseTest.env);
            testEnvironment = ConfigFactory.create(Environment.class);
           /* username1 = testEnvironment.autoUsername1();
            password1 = testEnvironment.autoPassword1();*/
            File file = new File(Paths.get("").toAbsolutePath().toString() + "/logs/");
            if (!file.exists())
                file.mkdir();
            String[] entries = file.list();
            for (String s : entries) {
                File currentFile = new File(file.getPath(), s);
                PrintWriter writer = null;
                try {
                    writer = new PrintWriter(currentFile);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                writer.print("");
                writer.close();
            }
            file = new File(Paths.get("").toAbsolutePath().toString() + "/screenshots/");
            FileUtils.deleteDirectory(file);
            file.mkdir();
            file = new File(Paths.get("").toAbsolutePath().toString() + "/download/");
            FileUtils.deleteDirectory(file);
            file.mkdir();
            FileUtils.deleteDirectory(new File(Paths.get("").toAbsolutePath().toString() + "/allure-results/"));
            // log = LogManager.getLogger(testName);
            BrowserDriverFactory factory = new BrowserDriverFactory(browser, log);
            if (run.equals("grid")) {
               // driver = factory.createDriverGrid();
            } else {
                driver = factory.createDriver();
            }
            driver.manage().window().maximize();
            log.info("Browser resolution:  " + driver.manage().window().getSize());
            java.awt.Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
            log.info("Current Screen resolution : " + "w : " + (short) size.getWidth() + " h : " + (short) size.getHeight());
            //setCurrentThreadName();
            // String testName = ctx.getCurrentXmlTest().getlog = new LoggerLog4j().initiateLogger();
            //driver.manage().window().setSize(new Dimension((short) size.getWidth(), (short) size.getHeight()));
            if (BrowserDriverFactory.OS.indexOf("win") >= 0)
                driver.manage().window().setSize(new Dimension(1400, 880));
            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
            driver.manage().timeouts().implicitlyWait(70, TimeUnit.SECONDS);
            //driver.manage().timeouts().setScriptTimeout(40, TimeUnit.SECONDS);
            log.info("Browser resolution:  " + driver.manage().window().getSize());
        } catch (Exception e) {
            log.error("**** Error in : setUp ***");
            TestUtilities.printTraceLog(e);
        }
    }
//    private void setCurrentThreadName() {
//        Thread thread = Thread.currentThread();
//        String threadName = thread.getName();
//        String threadId = String.valueOf(thread.getId());
//        if (!threadName.contains(threadId)) {
//            thread.setName(threadName + " " + threadId);
//        }
//    }


   /* @AfterMethod
    @Parameters({"environment"})
    public void afterMethod(String environment){
        Environment testEnvironment = ConfigFactory.create(Environment.class);
        ConfigFactory.setProperty("env", environment);
        testEnvironment = ConfigFactory.create(Environment.class);
        driver.navigate().to(testEnvironment.workspaceurl());
    }*/


    @AfterSuite(alwaysRun = true)
    //@AfterTest(alwaysRun = true)
    public void tearDown() {
        log.info("Closing final driver.");
        // Close browser
        //driver.quit();
        File dir = new File(System.getProperty("user.dir") + File.separator + "screenshots");
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        Date date = new Date();
        String zipDirName = System.getProperty("user.dir") + File.separator + formatter.format(date) + "screenshots.zip";
        new TestUtilities().zipDirectory(dir, zipDirName);
    }
}