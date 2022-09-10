package com.automation.listeners;

import com.automation.factory.DriverFactory;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.automation.factory.DriverFactory.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

public class ExtentReportListener implements ITestListener {

    private static final String REPORT_OUTPUT_FOLDER = System.getProperty("user.dir")+"/reports/";

    private static final String FILE_NAME = "TestExecutionReport.html";

    private static ExtentReports extentReports = initReports();

    public static ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<ExtentTest>();
    private static ExtentReports reports;

    private static ExtentReports initReports() {
        Path path = Paths.get(REPORT_OUTPUT_FOLDER);
        if(Files.exists(path)){
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        extentReports = new ExtentReports();
        ExtentSparkReporter reporter = new ExtentSparkReporter(REPORT_OUTPUT_FOLDER+FILE_NAME);
        reporter.config().setReportName("Ecommerce Automation Test Results");
        extentReports.attachReporter(reporter);
        extentReports.setSystemInfo("System", "Windows 10");
        extentReports.setSystemInfo("Author", "Prashant Singh");
        extentReports.setSystemInfo("Build#", "1.1");
        extentReports.setSystemInfo("Team", "Automation QA Team");
        extentReports.setSystemInfo("Customer Name", "A2Z");

        return  extentReports;
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
        String methodName = iTestResult.getMethod().getMethodName();
        String qualifiedName = iTestResult.getMethod().getQualifiedName();
        int last = qualifiedName.lastIndexOf(".");
        int mid = qualifiedName.substring(0, last).lastIndexOf(".");
        String className = qualifiedName.substring(mid+1, last);
        System.out.println(methodName+" started!");
        ExtentTest extentTest = extentReports.createTest(iTestResult.getMethod().getMethodName(), iTestResult.getMethod().getDescription());
        extentTest.assignCategory(iTestResult.getTestContext().getSuite().getName());
        extentTest.assignCategory(className);
        testThreadLocal.set(extentTest);
        testThreadLocal.get().getModel().setStartTime(getTime(iTestResult.getEndMillis()));

    }

    private Date getTime(long endMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endMillis);
        return calendar.getTime();
    }

    @Override
    public synchronized void onTestSuccess(ITestResult iTestResult) {
        System.out.println(iTestResult.getMethod().getMethodName()+" passed!");
        testThreadLocal.get().pass("Test passed");
        testThreadLocal.get().getModel().setEndTime(getTime(iTestResult.getEndMillis()));
    }

    @Override
    public synchronized void onTestFailure(ITestResult iTestResult) {
        System.out.println(iTestResult.getMethod().getMethodName()+" failed!");
        String methodName = iTestResult.getMethod().getMethodName();

        testThreadLocal.get().fail(iTestResult.getThrowable(), MediaEntityBuilder.createScreenCaptureFromPath(DriverFactory.getScreenShot(methodName)).build());
        testThreadLocal.get().getModel().setEndTime(getTime(iTestResult.getEndMillis()));
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        System.out.println("OnTestFailedButWithinSuccessPercentage for "+iTestResult.getMethod().getMethodName());
    }

    @Override
    public void onStart(ITestContext iTestContext) {
        System.out.println("Test suite started");
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        System.out.println("Test Suite is ending");
        reports.flush();
        testThreadLocal.remove();
    }
}
