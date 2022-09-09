package com.automation.factory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;

public class DriverFactory {
    public static ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<WebDriver>();
    public static Logger log = Logger.getLogger(DriverFactory.class);
    private static final String DRIVER_EXECUTABLE_PATH = System.getProperty("user.dir") + "//src//main//resources//driver//";
    public WebDriver webDriver;
    public Properties properties;
    public OptionsManager optionsManager;

    public static synchronized WebDriver getDriver() {
        return webDriverThreadLocal.get();
    }

    public static String getScreenShot(String methodName) {
        File screenshotFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
        SessionId sessionId = ((RemoteWebDriver) getDriver()).getSessionId();
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS");
        String screenshotFileName = String.format("%s-%s-%s.png", methodName, dateFormat.format(today), sessionId.toString());
        Path destination = Paths.get(System.getProperty("user.dir") + "//screenshot//" + screenshotFileName);

        try {
            Files.move(screenshotFile.toPath(), destination);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return destination.toString();
    }

    public WebDriver initDriver(Properties properties) {
        String browserName = properties.getProperty("browser");
        log.info("browser name is : " + browserName);
        optionsManager = new OptionsManager(properties);

        if (browserName.equalsIgnoreCase("chrome")) {
            log.info("running test on chrome");

            if (Boolean.parseBoolean(properties.getProperty("remote"))) {
                initRemoteDriver("chrome");
            } else {
                log.info("Running Tests on chrome -- local");
                WebDriverManager.chromedriver().setup();
                System.setProperty("webdriver.chrome.driver", DRIVER_EXECUTABLE_PATH + "chromedriver.exe");
                webDriverThreadLocal.set(new ChromeDriver(optionsManager.getChromeOptions()));
            }
        } else if (browserName.equalsIgnoreCase("firefox")) {
            if (Boolean.parseBoolean(properties.getProperty("remote"))) {
                initRemoteDriver("firefox");
            } else {
                WebDriverManager.firefoxdriver().setup();
                webDriverThreadLocal.set(new FirefoxDriver((optionsManager.getFirefoxOptions())));
            }
        } else if (browserName.equalsIgnoreCase("edge")) {
            if (Boolean.parseBoolean(properties.getProperty("remote"))) {
                initRemoteDriver("edge");
            } else {
                WebDriverManager.edgedriver().setup();
                webDriverThreadLocal.set(new EdgeDriver(optionsManager.getEdgeOptions()));
            }
        }

        getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        getDriver().manage().deleteAllCookies();
        getDriver().manage().window().maximize();
        getDriver().get(properties.getProperty("baseurl"));
        return getDriver();
    }

    private void initRemoteDriver(String browerName) {
        log.info("====================Runnning Tests on Selenium GRID - Remote Machine ==================");
        if (browerName.equalsIgnoreCase("chrome")) {
            try {
                webDriverThreadLocal.set(new RemoteWebDriver(new URL(properties.getProperty("huburl")), optionsManager.getChromeOptions()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else if (browerName.equalsIgnoreCase("firefox")) {
            try {
                webDriverThreadLocal.set(new RemoteWebDriver(new URL(properties.getProperty("huburl")), optionsManager.getFirefoxOptions()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else if (browerName.equalsIgnoreCase("edge")) {
            try {
                webDriverThreadLocal.set(new RemoteWebDriver(new URL(properties.getProperty("huburl")), optionsManager.getEdgeOptions()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public Properties initProp() {
        String configDirectoryPath = System.getProperty("user.dir") + "//src//test//resources//config";
        properties = new Properties();
        FileInputStream fileInputStream = null;

        String environmentName = System.getProperty("env");
        if (environmentName == null) {
            System.out.println("No env is given .... hence running it on QA env by default . . .");
            try {
                fileInputStream = new FileInputStream(configDirectoryPath + "//qa.config.properties");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                switch (environmentName.toLowerCase()) {
                    case "qa":
                        fileInputStream = new FileInputStream(configDirectoryPath + "//qa.config.properties");
                        break;
                    case "stage":
                        fileInputStream = new FileInputStream(configDirectoryPath + "//stage.config.properties");
                        break;
                    case "dev":
                        fileInputStream = new FileInputStream(configDirectoryPath + "//dev.config.properties");
                        break;
                    case "prod":
                        fileInputStream = new FileInputStream(configDirectoryPath + "//config.properties");
                        break;
                    case "uat":
                        fileInputStream = new FileInputStream(configDirectoryPath + "//uat.config.properties");
                        break;
                    default:
                        System.out.println("Please Pass Right Environment...." + environmentName);
                        break;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

}
