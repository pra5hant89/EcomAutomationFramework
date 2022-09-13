package com.automation.utils;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ElementUtil {
    public static Logger logger = Logger.getLogger(ElementUtil.class);
    private final WebDriver webDriver;
    private final Actions actions;

    public ElementUtil(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.actions = new Actions(webDriver);
    }

    public By getBy(String locatorType, String selector) {
        By locator = null;

        switch (locatorType.toLowerCase()) {
            case "id":
                locator = By.id(selector);
                break;

            case "name":
                locator = By.name(selector);
                break;

            case "class":
                locator = By.className(selector);
                break;

            case "xpath":
                locator = By.xpath(selector);
                break;
            case "cssselector":
                locator = By.cssSelector(selector);
                break;
            case "linktext":
                locator = By.linkText(selector);
                break;
            case "partiallinktext":
                locator = By.partialLinkText(selector);
                break;
            case "tagname":
                locator = By.tagName(selector);
                break;

            default:
                break;
        }

        return locator;
    }

    public void doSendKeys(String locatorType, String selector, String value) {
        By locator = getBy(locatorType, selector);
        getElement(locator).sendKeys(value);
    }

    public void doSendKeys(By locator, String value) {
        WebElement webElement = getElement(locator);
        webElement.clear();
        webElement.sendKeys(value);
    }

    public void doActionsSendKeys(By locator, String value) {
        actions.sendKeys(getElement(locator), value).perform();
    }

    public void doActionsClick(By locator) {
        actions.click(getElement(locator)).perform();
    }

    public void doClick(String locatorType, String selector) {
        By locator = getBy(locatorType, selector);
        getElement(locator).click();
    }

    public void doClick(By locator) {
        getElement(locator).click();
    }

    private WebElement getElement(By locator) {
        logger.info("By locator is: " + locator);
        return webDriver.findElement(locator);
    }

    public String doGetAttributeValue(By locator, String attributeName){
        return getElement(locator).getAttribute(attributeName);
    }
    public String doElementGetText(By locator){
        String elementText = getElement(locator).getText();
        return elementText;
    }
    public int getElementsTextCount(By locator){
        return getElementsTextList(locator).size();
    }

    private List<String> getElementsTextList(By locator) {
        List<WebElement> elementList = getElements(locator);
        List<String> elementTextList = new ArrayList<String>();
        for(WebElement element: elementList){
            String text = element.getText();
            if(!text.isEmpty()){
                elementTextList.add(text);
            }
        }
        return elementTextList;
    }

    public int getEmptyElementTextList(By locator){
        int totalLinks = getPageElementsTextCount(locator);
        int totalNonEmptyLinks = getElementsTextCount(locator);
        return totalLinks - totalNonEmptyLinks;
    }

    private int getPageElementsTextCount(By locator) {
        return getElements(locator).size();
    }

    private List<WebElement> getElements(By locator) {
        return webDriver.findElements(locator);
    }
}
