package com.zeidler.base;

import com.testautomationguru.utility.PDFUtil;
import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.testng.annotations.DataProvider;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class TestUtilities extends BaseTest {

    List<String> filesListInDir = new ArrayList<String>();

    @DataProvider(name = "files")
    protected static Object[][] files() {
        return new Object[][]{
                {1, "index.html"},
                {2, "logo.png"},
                {3, "text.txt"}
        };
    }

    /**
     * Today's date in yyyyMMdd format
     */
    public static String getTodaysDate() {
        return (new SimpleDateFormat("yyyyMMdd").format(new Date()));
    }

    /**
     * Current time in HHmmssSSS
     */
    public static String getSystemTime() {
        return (new SimpleDateFormat("HHmmssSSS").format(new Date()));
    }

    /**
     * To print exception in log file.
     *
     * @parame
     */
    public static void printTraceLog(Exception e) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
            log.info("Error URL is : " + BaseTest.driver.getCurrentUrl());
            Date date = new Date();
            new TestUtilities().takeScreenshot(formatter.format(date));
            // Allure.addAttachment(new TestListener().testMethodName, new ByteArrayInputStream(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)));
            Allure.addAttachment(formatter.format(date) + "_" + BaseTest.driver.getCurrentUrl(), new TestListener().saveScreenshot(formatter.format(date)));
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            log.error(exceptionAsString);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * This method compresses the single file to zip format
     *
     * @param file
     * @param zipFileName
     */
    public static void zipSingleFile(File file, String zipFileName) {
        try {
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            //add a new Zip Entry to the ZipOutputStream
            ZipEntry ze = new ZipEntry(file.getName());
            zos.putNextEntry(ze);
            //read the file and write to ZipOutputStream
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            //Close the zip entry to write to zip file
            zos.closeEntry();
            //Close resources
            zos.close();
            fis.close();
            fos.close();
            System.out.println(file.getCanonicalPath() + " is zipped to " + zipFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // STATIC SLEEP
    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get future dates
     */
    private Date tomorrow() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +1);
        return cal.getTime();
    }

    /**
     * Get logs from browser console
     */
    protected List<LogEntry> getBrowserLogs() {
        LogEntries log = driver.manage().logs().get("browser");
        List<LogEntry> logList = log.getAll();
        return logList;
    }

    /**
     * This method is to download all images from PDF file. Please pass pdfPath with .pdf only
     *
     * @param pdfPath
     * @param storagePath
     * @return
     */
    public boolean getAllImagesFromPDF(String pdfPath, String storagePath) {
        try {
            PDFUtil pdfUtil = new PDFUtil();
            //set output path
            File f1 = new File(storagePath);
            pdfUtil.setImageDestinationPath(f1.getAbsolutePath());
            //save all images
            File f2 = new File(pdfPath);
            pdfUtil.extractImages(f2.getAbsolutePath());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get the newest file for a specific extension
     * filePath is the path of directory
     *
     * @param filePath
     * @param ext
     * @return
     */
    public File getTheNewestFile(String filePath, String ext) {
        File theNewestFile = null;
        File dir = new File(filePath);
        FileFilter fileFilter = new WildcardFileFilter("*." + ext);
        File[] files = dir.listFiles(fileFilter);
        if (files.length > 0) {
            /** The newest file comes first **/
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            theNewestFile = files[0];
        }
        return theNewestFile;
    }


    /**
     * Take screenshot
     */
    public void takeScreenshot(String fileName) {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String path = System.getProperty("user.dir")
                //    + File.separator + "test-output"
                + File.separator + "screenshots"
                + File.separator + getTodaysDate()
                + File.separator + " " + fileName + ".png";
        try {
            FileUtils.copyFile(scrFile, new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method zips the directory
     *
     * @param dir
     * @param zipDirName
     */
    public boolean zipDirectory(File dir, String zipDirName) {
        try {
            populateFilesList(dir);
            if (filesListInDir.size() <= 0)
                return false;
            //now zip files one by one
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (String filePath : filesListInDir) {
                System.out.println("Zipping :: " + filePath);
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
                zos.putNextEntry(ze);
                //read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * This method populates all the files in a directory to a List
     *
     * @param dir
     * @throws IOException
     */
    public void populateFilesList(File dir) throws IOException {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) filesListInDir.add(file.getAbsolutePath());
            else populateFilesList(file);
        }
    }


    public void clearWebSiteData(WebDriver driver) {
        try {
            log.info("Function for clearing web site data");
            driver.get("chrome://settings/?search=clear");
            sleep(1000);
            WebElement root1 = driver.findElement(By.tagName("settings-ui"));
            Thread.sleep(300);
            WebElement shadowRoot1 = expandRootElement(root1);
            WebElement root2 = shadowRoot1.findElement(By.cssSelector("#main"));
            WebElement shadowRoot2 = expandRootElement(root2);
            WebElement root3 = shadowRoot2.findElement(By.cssSelector("settings-basic-page"));
            WebElement shadowRoot3 = expandRootElement(root3);
            //WebElement root4 = shadowRoot3.findElement(By.cssSelector("#advancedPage > settings-section[section='privacy']"));
            //WebElement shadowRoot4 = expandRootElement(root4);
            WebElement root5 = shadowRoot3.findElement(By.cssSelector("#advancedPage > settings-section[section='privacy'] > settings-privacy-page"));
            WebElement shadowRoot5 = expandRootElement(root5);
            WebElement root6 = shadowRoot5.findElement(By.cssSelector("#clearBrowsingData"));
            Thread.sleep(300);
            root6.click();
            Thread.sleep(500);
            WebElement shadowRoot6 = expandRootElement(root5);
            WebElement root7 = shadowRoot6.findElement(By.cssSelector("settings-clear-browsing-data-dialog"));
            WebElement shadowRoot7 = expandRootElement(root7);
            WebElement root8 = shadowRoot7.findElement(By.cssSelector("#clearBrowsingDataConfirm"));
            Thread.sleep(300);
            root8.click();
            sleep(10000);
            log.info("Finished clearing web site data");
        } catch (Exception e) {
            TestUtilities.printTraceLog(e);
        }
    }

    //Returns webelement
    public WebElement expandRootElement(WebElement element) {
        WebElement ele = (WebElement) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot", element);
        return ele;
    }

    /**
     * This method is to close all tabs and clear browsing history from the current driver
     *
     * @param driver
     * @return
     */
    public boolean clearHistoryCloseAllTabs(WebDriver driver) {
        ArrayList<String> tabs_windows = null;
        try {
            clearWebSiteData(driver);
            sleep(500);
            tabs_windows = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(tabs_windows.get(0));
            int limit = tabs_windows.size();
            for (int i = 1; i <= limit - 1; i++) {
                driver.switchTo().window(tabs_windows.get(i));
                driver.close();
            }
        } catch (Exception e) {
            return false;
        } finally {
            try {
                driver.switchTo().window(tabs_windows.get(0));
            } catch (Exception e1) {
                return false;
            }
        }
        return true;
    }

    public void JSClick(By locator) {
        WebElement element = driver.findElement(locator);
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }


}