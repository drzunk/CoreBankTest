package com.corebank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
    private WebDriver driver;

    // 1. Cất giữ Locator (Bản đồ tìm kiếm)
    private By txtUsername = By.name("username");
    private By txtPassword = By.name("password");
    private By btnLogin = By.xpath("//input[@value='Log In']");

    // 2. Constructor
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    // 3. Các hành động (Actions)
    public void openPage() {
        driver.get("https://parabank.parasoft.com/parabank/index.htm");
    }

    public void login(String username, String password) {
        driver.findElement(txtUsername).sendKeys(username);
        driver.findElement(txtPassword).sendKeys(password);
        driver.findElement(btnLogin).click();

        // --- CHỐNG FLAKY TEST TẠI ĐÂY ---
        // Ép con Bot phải đứng chờ cho đến khi nút "Log Out" hiện ra (nghĩa là đã login xong 100%)
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(By.linkText("Log Out")));
    }
}