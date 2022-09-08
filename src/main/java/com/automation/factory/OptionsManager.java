package com.automation.factory;

import org.apache.log4j.Logger;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Properties;

public class OptionsManager {
    private Properties properties;
    private ChromeOptions chromeOptions;
    private FirefoxOptions firefoxOptions;
    private EdgeOptions edgeOptions;

    private static Logger log = Logger.getLogger(OptionsManager.class);

    public OptionsManager(Properties properties) {
        this.properties = properties;
    }

    public ChromeOptions getChromeOptions() {

        log.info("Adding Chrome Options....");
        chromeOptions = new ChromeOptions();
        if (Boolean.parseBoolean(properties.getProperty("headless")))
            chromeOptions.addArguments("--headless");
        if (Boolean.parseBoolean(properties.getProperty("incognito")))
            chromeOptions.addArguments("--incognito");
        if (Boolean.parseBoolean(properties.getProperty("remote"))) {
            chromeOptions.setPlatformName("linux");
            chromeOptions.setCapability("enableVNC", true);
        }
        chromeOptions.addArguments("--window-size=1280,800");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--disable-setuid-sandbox");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments(("--whitelisted-ips="));
        chromeOptions.addArguments("--disable-extensions");
        return chromeOptions;

    }

    public FirefoxOptions getFirefoxOptions() {
        firefoxOptions = new FirefoxOptions();
        if (Boolean.parseBoolean(properties.getProperty("headless")))
            firefoxOptions.addArguments("--headless");
        if (Boolean.parseBoolean(properties.getProperty("incognito")))
            firefoxOptions.addArguments("--incognito");

        if (Boolean.parseBoolean(properties.getProperty("remote"))) {
            firefoxOptions.setPlatformName("linux");
            firefoxOptions.setCapability("enableVNC", true);
        }

        return firefoxOptions;
    }

    public EdgeOptions getEdgeOptions() {
        edgeOptions = new EdgeOptions();
        if (Boolean.parseBoolean(properties.getProperty("headless"))) {
            edgeOptions.addArguments("--headless");
        }
        if (Boolean.parseBoolean(properties.getProperty("incognito"))) {
            edgeOptions.addArguments("--incognito");
        }
        if (Boolean.parseBoolean(properties.getProperty("remote"))) {
            edgeOptions.setPlatformName("linux");
            edgeOptions.setCapability("enableVNC", true);
            edgeOptions.setBrowserVersion(properties.getProperty("browserversion"));
            edgeOptions.setCapability("se:timezone", "US/Pacific");
            edgeOptions.setCapability("se:screenResolution", "1920x1080");
        }
        return edgeOptions;
    }
}
