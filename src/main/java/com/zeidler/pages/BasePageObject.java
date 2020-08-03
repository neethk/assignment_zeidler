package com.zeidler.pages;

import com.zeidler.base.BaseTest;
import com.zeidler.base.TestUtilities;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class BasePageObject {
    public static WebDriver driver;
    static int icount = 0;
    public Logger log;

    public BasePageObject(WebDriver driver, Logger log) {
        this.driver = BaseTest.driver;
        this.log = BaseTest.log;
    }


    /**
     * This method is to verify element is present or not
     *
     * @param locator
     * @return
     */


    public boolean checkTextPresent(By locator){
        driver.manage().timeouts().implicitlyWait(2,TimeUnit.SECONDS);
        ArrayList list=(ArrayList)driver.findElements(locator);
        if(list.size()>0)
            return true;

        return false;

    }



    public void waitForVisibilityOf(By locator, Integer... timeOutInSeconds) {
        int attempts = 0;
        while (attempts < 2) {
            try {
                waitFor(ExpectedConditions.visibilityOfElementLocated(locator),
                        (timeOutInSeconds.length > 0 ? timeOutInSeconds[0] : null));
                break;
            } catch (Exception e) {
            }
            attempts++;
        }
    }

    /**
     * This method is to verify element is present or not
     *
     * @param locator
     * @return
     */
    public static boolean checkElementPresentWithTime(By locator, int time) {
        boolean bstatus = false;
        try {
            driver.manage().timeouts().implicitlyWait(time, TimeUnit.SECONDS);
            if (driver.findElement(locator).isDisplayed())
                bstatus = true;
        } catch (Exception e) {
        } finally {
            driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
            return bstatus;
        }
    }

    /**
     * Open page with given URL
     */
    public void openUrl(String url) {
        driver.get(url);
    }

    /**
     * Find element using given locator
     */
    public WebElement find(By locator) {
        return driver.findElement(locator);
    }


    /**
     * Click on element with given locator when its visible
     */
    public void click(By locator) throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (Exception e) {
        }
        driver.findElement(locator).click();
        sleep(1000);
    }



    public void clickandrefresh(By locator) throws Exception {
        String a = locator.toString().split("'")[1];
        String b = driver.findElement(locator).getAttribute("innerHTML");
        if (driver.findElement(locator).getText().contains(b)) {
            waitForVisibilityOf(locator, 10);
            waitForClickable(locator, 10);
            find(locator).click();
        } else {
            if (icount < 10) {
                driver.navigate().refresh();
                Thread.sleep(3000);
                icount = icount + 1;
                clickandrefresh(locator);
            }
        }
    }

    /**
     * Type given text into element with given locator
     */
    public void type(String text, By locator) {
        waitForVisibilityOf(locator, 5);
        find(locator).sendKeys(text);
    }

    /**
     * Clear text
     */
    public void clear(By locator) {
        waitForVisibilityOf(locator, 5);
        getElement(locator).clear();
    }

    public void typeText(String text, By locator) {
        Actions actions = new Actions(driver);
        WebElement element = driver.findElement(locator);
        actions.sendKeys(element, text).build().perform();
    }

    public void sendChar(By locator, String value) {
        find(locator).clear();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            String s = new StringBuilder().append(c).toString();
            find(locator).sendKeys(s);
        }
    }

    /**
     * Get URL of current page from browser
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Get title of current page
     */
    public String getCurrentPageTitle() {
        return driver.getTitle();
    }

    /**
     * Get source of current page
     */
    public String getCurrentPageSource() {
        return driver.getPageSource();
    }

    /**
     * Wait for specific ExpectedCondition for the given amount of time in seconds
     */
    private void waitFor(ExpectedCondition<WebElement> condition, Integer timeOutInSeconds) {
        timeOutInSeconds = timeOutInSeconds != null ? timeOutInSeconds : 30;
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        wait.until(condition);
    }

    /**
     * Wait for given number of seconds for element with given locator to be visible
     * on the page
     */

    public void typeString(String text, By locator) {
        waitForVisibilityOf(locator, 5);
        for (char s : text.toCharArray())
            find(locator).sendKeys(String.valueOf(s));
    }


    public void switchToWindowWithTitle(String expectedTitle) {
        // Switching to new window
        String firstWindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles();
        Iterator<String> windowsIterator = allWindows.iterator();
        while (windowsIterator.hasNext()) {
            String windowHandle = windowsIterator.next().toString();
            if (!windowHandle.equals(firstWindow)) {
                driver.switchTo().window(windowHandle);
                if (getCurrentPageTitle().equals(expectedTitle)) {
                    break;
                }
            }
        }
    }

    public void switchToWindowWithIndex(String expectedIndex) {
        // Switching to new window
        String firstWindow = driver.getWindowHandle();
    }

    /*
     * Switch to iFrame using it's locator
     */
    public void switchToFrame(By frameLocator) {
        driver.switchTo().frame(find(frameLocator));
    }



    /**
     * Perform scroll to the bottom
     */
    public void scrollToBottom() {
        log.info("Scrolling to the bottom of the page");
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    /**
     * Add cookie
     */
    public void setCookie(Cookie ck) {
        log.info("Adding cookie " + ck.getName());
        driver.manage().addCookie(ck);
        log.info("Cookie added");
    }

    /**
     * Get cookie value using cookie name
     */
    public String getCookie(String name) {
        log.info("Getting value of cookie " + name);
        return driver.manage().getCookieNamed(name).getValue();
    }

    /**
     * Wait for element to be clickable
     */
    public void waitForClickable(By locator, Integer timeOutInSeconds) throws Exception {
        waitFor(ExpectedConditions.elementToBeClickable(locator), timeOutInSeconds);
    }

    /**
     * This method with enter
     *
     * @param webElement
     * @param input
     * @throws Exception
     */
    public void sendCharByChar(By webElement, String input) throws Exception {
        log.info("**** Started : sendCharByChar ***");
        log.info("Input is : " + input);
        waitForClickable(webElement, 20);
        click(webElement);
        if (!driver.findElement(webElement).getAttribute("value").contains(input)) {
            driver.findElement(webElement).clear();
            driver.findElement(webElement).clear();
            click(webElement);
            String temp = driver.findElement(webElement).getAttribute("value");
            for (int j = 0; j < temp.length(); j++) {
                driver.findElement(webElement).sendKeys(Keys.BACK_SPACE);
            }
            driver.findElement(webElement).clear();
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                driver.findElement(webElement).sendKeys(Character.toString(c));
                sleep(30);
            }
        }
        log.info("**** Ending : sendCharByChar ***");
    }

    public enum Action {
        WIN, MAC, LINUX, SEND_KEYS, FILE_DETECTOR;
    }


    /**
     * Wait for element to be present
     */
    public void waitForPresent(By locator, Integer timeOutInSeconds) throws Exception {
        waitFor(ExpectedConditions.presenceOfElementLocated(locator), timeOutInSeconds);
    }

    /**
     * This method is scroll to web element
     *
     * @param locator
     * @throws Exception
     */
    public void scrollToView(By locator) throws Exception {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        Thread.sleep(1500);
    }

    /**
     * To get xpath with anchor tag and text
     *
     * @param variable
     * @return
     */
    public String GetTextXpath(String variable) {
        String xpath = "//a[contains(text(),'" + variable + "')]";
        return xpath;
    }

    /**
     * @param locator
     * @return
     */
    public String getTextFromElement(By locator) {
        return driver.findElement(locator).getText();
    }


    /**
     * @param locator
     * @param attribute
     * @return
     */
    public String getAttributeFromElement(By locator, String attribute) {
        return driver.findElement(locator).getAttribute(attribute);
    }

    /**
     * @param locator
     * @return
     */
    public WebElement getElement(By locator) {
        return driver.findElement(locator);
    }

    public boolean visibilityCheck(By locator) {
        if (find(locator).isDisplayed())
            return true;
        else
            return false;
    }
}
