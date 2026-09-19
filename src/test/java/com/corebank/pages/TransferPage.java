package com.corebank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TransferPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // 1. Cất giữ Locator (Bản đồ)
    private By menuTransfer = By.linkText("Transfer Funds");
    private By txtAmount = By.id("amount");
    private By ddlFromAccount = By.id("fromAccountId");
    private By ddlToAccount = By.id("toAccountId");
    private By btnTransfer = By.xpath("//input[@value='Transfer']");
    private By msgSuccess = By.xpath("//h1[normalize-space()='Transfer Complete!']");
    private By titleTransfer = By.xpath("//h1[normalize-space()='Transfer Funds']");

    // 2. Constructor
    public TransferPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // 3. Các hành động (Actions)
    public void goToTransferFunds() throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(menuTransfer)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(titleTransfer));
        // Đợi 2 giây cho AJAX tải xong danh sách tài khoản
        Thread.sleep(2000);
    }

    public void transferMoney(String amount, String fromAccId, String toAccId) {
        driver.findElement(txtAmount).sendKeys(amount);

        // Chọn tài khoản từ Dropdown
        new Select(driver.findElement(ddlFromAccount)).selectByVisibleText(fromAccId);
        new Select(driver.findElement(ddlToAccount)).selectByVisibleText(toAccId);

        driver.findElement(btnTransfer).click();
    }

    public boolean isTransferComplete() {
        WebElement successElement = wait.until(ExpectedConditions.visibilityOfElementLocated(msgSuccess));
        return successElement.isDisplayed();
    }
}