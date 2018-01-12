import io.github.bonigarcia.wdm.PhantomJsDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PontoDump {

    public static void Dump(String address, String user, String password, String startDate, String finalDate) {
        WebDriver driver = null;

        try {
            DesiredCapabilities desiredCapabilities = SetupBrowser();

            driver = CreateWebDriver(desiredCapabilities);

            JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;

            WebDriverWait wait = new WebDriverWait(driver, 5);

            LoginTraceGP(address, user, password, driver, javascriptExecutor, wait);

            PerformQuery(startDate, finalDate, driver, javascriptExecutor, wait);

            DumpRecords(driver);

            TakeScreenshot((TakesScreenshot) driver);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private static WebDriver CreateWebDriver(DesiredCapabilities desiredCapabilities) {
        WebDriver driver;
        driver = new PhantomJSDriver(desiredCapabilities);
        driver.manage().window().setSize(new Dimension(1366, 768));
        return driver;
    }

    private static void TakeScreenshot(TakesScreenshot driver) {
        File tempFile = driver.getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(tempFile, new File("Ponto.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void DumpRecords(WebDriver driver) {
        List<WebElement> lines = driver.findElements(By.xpath("//*[@id='listagem']/tbody/tr"));

        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Ponto.csv", false), StandardCharsets.UTF_8))) {

            bufferedWriter.append("Data,Entrada,Saída,Trabalhado\n");

            for (WebElement line : lines) {
                List<WebElement> columns = line.findElements(By.tagName("td"));

                String dateColumnText = columns.get(1).getText();
                if (!dateColumnText.matches("\\d{2}/\\d{2}/\\d{4}")) {
                    continue;
                }
                for (int x = 1; x <= 4; x++) {
                    bufferedWriter.append(columns.get(x).getText().trim());
                    if (x < 4) {
                        bufferedWriter.append(",");
                    }
                }

                bufferedWriter.append("\n");
                bufferedWriter.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void PerformQuery(String startDate, String finalDate, WebDriver driver, JavascriptExecutor javascriptExecutor, WebDriverWait wait) {
        WebElement periodoInicial = driver.findElement(By.id("periodoInicial"));
        WebElement periodoFinal = driver.findElement(By.id("periodoFinal"));

        javascriptExecutor.executeScript("document.getElementById('periodoInicial').setAttribute('type', 'text');");
        periodoInicial.clear();
        periodoInicial.sendKeys(startDate);

        javascriptExecutor.executeScript("document.getElementById('periodoFinal').setAttribute('type', 'text');");
        periodoFinal.clear();
        periodoFinal.sendKeys(finalDate);

        driver.findElement(By.id("botaoConsultar")).click();

        wait.until(x -> javascriptExecutor.executeScript("return document.readyState").equals("complete"));

    }

    private static void LoginTraceGP(String address, String user, String password, WebDriver driver, JavascriptExecutor javascriptExecutor, WebDriverWait wait) {
        driver.get(String.format("%s/indexPopup.jsp", address));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("txtUsuario")));
        driver.findElement(By.id("txtUsuario")).sendKeys(user);

        wait.until(ExpectedConditions.elementToBeClickable(By.id("pwdSenha")));
        driver.findElement(By.id("pwdSenha")).sendKeys(password);

        wait.until(ExpectedConditions.elementToBeClickable(By.id("botaoAcessarLogin")));
        driver.findElement(By.id("botaoAcessarLogin")).click();

        driver.get(String.format("%s/PontoUsuario.do", address));

        wait.until(x -> javascriptExecutor.executeScript("return document.readyState").equals("complete"));
    }

    private static DesiredCapabilities SetupBrowser() {
        PhantomJsDriverManager.getInstance().setup();

        DesiredCapabilities desiredCapabilities = DesiredCapabilities.phantomjs();
        desiredCapabilities.setCapability("takesScreenshot", true);

        File phanthomJSBinaryPath = new File(PhantomJsDriverManager.getInstance().getBinaryPath());
        desiredCapabilities.setCapability("phantomjs.binary.path", phanthomJSBinaryPath.getAbsolutePath());

        ArrayList<String> phantomJSArgument = new ArrayList<>();
        phantomJSArgument.add("--ignore-ssl-errors=true");
        phantomJSArgument.add("--ssl-protocol=any");
        phantomJSArgument.add("--proxy-type=none");

        desiredCapabilities.setCapability("phantomjs.cli.args", phantomJSArgument);
        return desiredCapabilities;
    }
}
