package com.github.javister.docker.testing.wildfly;

import com.github.javister.docker.testing.openjdk.JavisterOpenJDKContainer;
import com.github.javister.docker.testing.selenium.JavisterWebDriverConfigurator;
import com.github.javister.docker.testing.selenium.JavisterWebDriverContainer;
import com.github.javister.docker.testing.selenium.JavisterWebDriverProvider;
import com.github.javister.docker.testing.wildfly.containers.WildflyHibernateContainer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

@Testcontainers
class HibernateIT {
    @Container
    private static final WildflyHibernateContainer application =
            new WildflyHibernateContainer(
                    JavisterOpenJDKContainer.Variant.V8,
                    JavisterWildflyContainer.Variant.WILDFLY8,
                    HibernateIT.class
            )
                    .withInstanceId("1")
            // Активировать, если надо поотлаживать приложение, стартуемое в контейнере
            // Или установить в OS переменную окружения DOCKER_WILDFLY_DEBUG_SUSPEND в значение True
            /*.withDebugSuspend(true)*/
            // Активировать, если надо поотлаживать приложение на определённом порту
            // Или установить в OS переменную окружения DOCKER_WILDFLY_DEBUG_PORT в значение требуемого порта
            /*.withDebugPort(9009)*/;

    private static final AtomicInteger userId = new AtomicInteger(2);
    private static final AtomicInteger userSuffix = new AtomicInteger('A');

    @JavisterWebDriverConfigurator
    @SuppressWarnings({"unused", "RedundantSuppression"})
    void webDriverConfig(JavisterWebDriverContainer webContainer) {
        webContainer
                .withImplicitlyWait(15_000L)
                .withApplication(application);
    }

    @SuppressWarnings("squid:S2699")
    @JavisterWebDriverProvider
    void testRegisterUsers(JavisterWebDriverContainer webContainer) throws InterruptedException {
        WebDriver driver = webContainer.getWebDriver();
        driver.get(application.getURL());
        registerUser(driver);
        registerUser(driver);
    }

    private void registerUser(WebDriver driver) throws InterruptedException {
        int id = userId.getAndIncrement();
        int suffix = userSuffix.getAndIncrement();
        driver.findElement(By.id("reg:id")).clear();
        driver.findElement(By.id("reg:id")).sendKeys(Integer.toString(id));
        driver.findElement(By.id("reg:name")).clear();
        driver.findElement(By.id("reg:name")).sendKeys("Test" + id);
        driver.findElement(By.id("reg:email")).clear();
        driver.findElement(By.id("reg:email")).sendKeys("test" + id + "@mail.com");
        driver.findElement(By.id("reg:phoneNumber")).clear();
        driver.findElement(By.id("reg:phoneNumber")).sendKeys(id + "000000000");
        driver.findElement(By.id("reg:address")).clear();
        driver.findElement(By.id("reg:address")).sendKeys("Test " + id);
        driver.findElement(By.id("reg:register")).click();
        assertEquals(
                "Должно показать ошибку формата ввода",
                "must contain only letters and spaces",
                driver.findElement(By.xpath("//span[@class='invalid']")).getText());

        driver.findElement(By.id("reg:name")).clear();
        Thread.sleep(1000);
        driver.findElement(By.id("reg:name")).sendKeys("Test " + (char) suffix);
        driver.findElement(By.id("reg:register")).click();

        int resultRow = (id + 1);

        assertEquals(
                "Первая колонка должны содержать ID записи",
                Integer.toString(id),
                driver.findElement(By.xpath("//table[@class='simpletablestyle']//tr[" + resultRow + "]/td[1]")).getText());
        assertEquals(
                "Вторая колонка должна содержать имя пользователя",
                "Test " + (char) suffix,
                driver.findElement(By.xpath("//table[@class='simpletablestyle']//tr[" + resultRow + "]/td[2]")).getText());
        assertEquals(
                "Третья колонка должна содержать электронный адрес пользователя",
                "test" + id + "@mail.com",
                driver.findElement(By.xpath("//table[@class='simpletablestyle']//tr[" + resultRow + "]/td[3]")).getText());
        assertEquals(
                "Четвёртая колонка должна содержать телефон пользователя",
                id + "000000000",
                driver.findElement(By.xpath("//table[@class='simpletablestyle']//tr[" + resultRow + "]/td[4]")).getText());
        assertEquals(
                "Пятая колонка должна содержать почтовый адрес пользователя",
                "Test " + id,
                driver.findElement(By.xpath("//table[@class='simpletablestyle']//tr[" + resultRow + "]/td[5]")).getText());
    }
}
