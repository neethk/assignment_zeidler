package com.zeidler.base;
import org.apache.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class BrowserDriverFactory {

    public static String downloadPath = Paths.get("").toAbsolutePath().toString() + File.separator + "download";
    //public static String logPath = Paths.get("").toAbsolutePath().toString() + "/logs/";
    public static String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
    static String chromePath = "src/main/resources/";
    static String geckoPath = "src/main/resources/";

    static {
        try {
            System.out.println("Download file path : " + downloadPath);
            File file = new File(downloadPath);
            if (!file.exists())
                file.mkdir();
            String[] entries = file.list();
            for (String s : entries) {
                File currentFile = new File(file.getPath(), s);
                currentFile.delete();
            }
            System.out.println(OS);
            Runtime rt = Runtime.getRuntime();
            if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
                chromePath = chromePath + "chromedriver";
                geckoPath = geckoPath + "geckodriver";
                rt.exec("pkill -f chromedriver");
                rt.exec("pkill -f geckodriver");
            } else if (OS.indexOf("win") >= 0) {
                ArrayList<String> output = new ArrayList<String>();
                Process p = Runtime.getRuntime().exec("REG QUERY HKEY_CURRENT_USER\\Software\\Google\\Chrome\\BLBeacon /v version");
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()), 8 * 1024);
                BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                String s = null;
                while ((s = stdInput.readLine()) != null) {
                    output.add(s);
                }
                String chrome_value = (output.get(2));
                String version = chrome_value.trim().split("   ")[2];
                version = version.trim();
                System.out.println("Installed chrome version : " + version);
                if (version.startsWith("78"))
                    chromePath = chromePath + "/chromedriver_data/78/" + "chromedriver.exe";
                else if (version.startsWith("79"))
                    chromePath = chromePath + "/chromedriver_data/79/" + "chromedriver.exe";
                else if (version.startsWith("80"))
                    chromePath = chromePath + "/chromedriver_data/80/" + "chromedriver.exe";
                else if (version.startsWith("81"))
                    chromePath = chromePath + "/chromedriver_data/81/" + "chromedriver.exe";
                else if (version.startsWith("83"))
                    chromePath = chromePath + "/chromedriver_data/83/" + "chromedriver.exe";
                else
                    chromePath = chromePath + "/chromedriver_data/80/" + "chromedriver.exe";
                System.out.println("ChromeDriver Path :: " + chromePath);
                geckoPath = geckoPath + "geckodriver.exe";
                rt.exec("taskkill /F /IM chromedriver.exe /T");
                rt.exec("taskkill /F /IM geckodriver.exe /T");
            } else if (OS.indexOf("nux") >= 0) {
                chromePath = chromePath + "chromedriver";
                geckoPath = geckoPath + "geckodriver";
            } else {
                chromePath = chromePath + "chromedriver";
                geckoPath = geckoPath + "geckodriver";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<RemoteWebDriver>();
    private String browser;
    private Logger log;

    public BrowserDriverFactory(String browser, Logger log) {
        //String browsername= System.getProperty("browser");
        log.info("browser" + browser);
        this.browser = browser.toLowerCase();
        this.log = BaseTest.log;
    }

    public WebDriver createDriver() throws Exception {
        // Create driver
        log.info("Create driver: " + browser);
        ChromeOptions chromeOptions = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("download.prompt_for_download", false);
        prefs.put("profile.default_content_settings.popups", 0);
        prefs.put("download.default_directory", downloadPath);
        chromeOptions.setExperimentalOption("prefs", prefs);
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation", "load-extension"});
        chromeOptions.addArguments("--disable-web-security");
        chromeOptions.addArguments("--no-proxy-server");
        chromeOptions.addArguments("--test-type");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--disable-features=VizDisplayCompositor");
        chromeOptions.addArguments("--disable-gpu");
        // chromeOptions.addArguments("chrome.switches","--disable-extensions");
        //chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        Capabilities cap = null;
        switch (browser) {
            case "chrome":
                System.setProperty("webdriver.chrome.driver", chromePath);
                driver.set(new ChromeDriver(chromeOptions));
                cap = driver.get().getCapabilities();
                log.info("Browser version is : " + cap.getVersion().toString());
                break;
            case "windowschrome":
                System.setProperty("webdriver.chrome.driver", chromePath);
                driver.set(new ChromeDriver(chromeOptions));
                cap = driver.get().getCapabilities();
                log.info("Browser version is : " + cap.getVersion().toString());
                break;
            case "firefox":
                System.setProperty("webdriver.gecko.driver", geckoPath);
                driver.set(new FirefoxDriver());
                cap = driver.get().getCapabilities();
                log.info("Browser version is : " + cap.getVersion().toString());
                break;
            case "chromeheadless":
                System.setProperty("webdriver.chrome.driver", chromePath);
                chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless");
                driver.set(new ChromeDriver(chromeOptions));
                break;
            case "firefoxheadless":
                System.setProperty("webdriver.gecko.driver", geckoPath);
                FirefoxBinary firefoxBinary = new FirefoxBinary();
                firefoxBinary.addCommandLineOptions("--headless");
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setBinary(firefoxBinary);
                driver.set(new FirefoxDriver(firefoxOptions));
                break;
            case "phantomjs":
                System.setProperty("phantomjs.binary.path", "src/main/resources/phantomjs.exe");
                driver.set(new PhantomJSDriver());
                break;
            default:
                System.out.println("Do not know how to start: " + browser + ", starting chrome.");
                System.setProperty("webdriver.chrome.driver", chromePath);
                //Add chrome switch to disable notification - "**--disable-notifications**"
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                chromeOptions.addArguments("--disable-web-security");
                chromeOptions.addArguments("--no-proxy-server");
                chromeOptions.setExperimentalOption("prefs", prefs);
                driver.set(new ChromeDriver(chromeOptions));
                cap = driver.get().getCapabilities();
                log.info("Browser version is : " + cap.getVersion().toString());
                break;
        }
        return driver.get();
    }

    public WebDriver createChromeWithProfile(String profile) {
        log.info("Starting chrome driver with profile: " + profile);
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.default_content_settings.popups", 0);
        prefs.put("download.default_directory", Paths.get("").toAbsolutePath().toString() + "/target/");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        chromeOptions.addArguments("user-data-dir=src/main/resources/Profiles/" + profile);
        chromeOptions.addArguments("--disable-web-security");
        chromeOptions.addArguments("--no-proxy-server");
        chromeOptions.setExperimentalOption("prefs", prefs);
        chromeOptions.addArguments("--disable-extensions");
        System.setProperty("webdriver.chrome.driver", chromePath);
        driver.set(new ChromeDriver(chromeOptions));
        return driver.get();
    }

    public WebDriver createChromeWithMobileEmulation(String deviceName) {
        log.info("Starting driver with " + deviceName + " emulation]");
        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", deviceName);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
        System.setProperty("webdriver.chrome.driver", chromePath);
        driver.set(new ChromeDriver(chromeOptions));
        return driver.get();
    }

    //http://192.168.215.23:4444/wd/hub
    //http://172.31.34.167:4444/wd/hub
    public WebDriver createDriverGrid() {
        String hubUrl = "http://192.168.215.23:4444/wd/hub";
        DesiredCapabilities capabilities = new DesiredCapabilities();
        System.out.println("Starting " + browser + " on grid");
        // Creating driver
        switch (browser) {
            case "chrome":
                capabilities.setBrowserName(DesiredCapabilities.chrome().getBrowserName());
                break;
            case "firefox":
                capabilities.setBrowserName(DesiredCapabilities.firefox().getBrowserName());
                break;
        }
        try {
            driver.set(new RemoteWebDriver(new URL(hubUrl), capabilities));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return driver.get();
    }
}
