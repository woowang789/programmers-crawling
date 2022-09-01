package woowang;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    final static private String OUTPUT_PATH = "data.json";
    final static private String DRIVER_PATH = "chromedriver";
    public static void main(String[] args) throws InterruptedException, IOException {
        Path path = Paths.get(System.getProperty("user.dir"), DRIVER_PATH);

        System.setProperty("webdriver.chrome.driver", path.toString());
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");            // 전체화면으로 실행
        options.addArguments("--disable-popup-blocking");    // 팝업 무시
        options.addArguments("--disable-default-apps");     // 기본앱 사용안함

        ChromeDriver driver = new ChromeDriver( options );

        driver.get("https://school.programmers.co.kr/learn/challenges");

        WebElement lastIdxEl = driver.findElement(new By.ByXPath("//*[@id=\"tab_all_challenges\"]/section/div[2]/div/div/div[1]/div/nav/ul/li[8]/a"));
        int lastIdx = Integer.parseInt(lastIdxEl.getText());

        List<Problem> problemList = new ArrayList<>();

        int curIdx = 0;
        while (curIdx < lastIdx) {
            Thread.sleep(1500);
            List<WebElement> problems = driver.findElements(new By.ByClassName("col-item"));

            for (int i = 1; i <= problems.size(); i++)
                problemList.add(parseProblem(problems.get(i - 1),i));

            clickNextBtn(driver);
            curIdx++;
        }
        driver.quit();

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        String s = mapper.writeValueAsString(problemList);
        System.out.println(s);

        FileWriter writer = new FileWriter(OUTPUT_PATH);
        writer.write(s);
        writer.flush();
        writer.close();

    }
    static private Problem parseProblem(WebElement problem,int i) {
        String url = problem.findElement(new By.ByXPath("//*[@id=\"tab_all_challenges\"]/section/div[2]/div/div/div[1]/section/div/div[" + i + "]/div/a"))
                .getAttribute("href");
        int pos = url.lastIndexOf("/");
        int id = Integer.parseInt(url.substring(pos+1));
        String title = problem.findElement(new By.ByXPath("//*[@id=\"tab_all_challenges\"]/section/div[2]/div/div/div[1]/section/div/div[" + i + "]/div/a/h4")).getText().substring(6);
        String level = problem.findElement(new By.ByXPath("//*[@id=\"tab_all_challenges\"]/section/div[2]/div/div/div[1]/section/div/div[" + i + "]/div/a/h4/span")).getText();
        return new Problem(id, title, url, level);
    }

    static private void clickNextBtn(ChromeDriver driver) {
        WebElement element = driver.findElement(new By.ByXPath("//*[@id=\"tab_all_challenges\"]/section/div[2]/div/div/div[1]/div/nav/ul"));
        List<WebElement> idxBtn = element.findElements(new By.ByClassName("page-item"));
        WebElement nextBtn = idxBtn.get(idxBtn.size() - 1);
        nextBtn.click();
    }
}