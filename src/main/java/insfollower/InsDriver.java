package insfollower;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class InsDriver {
    private final String USER_NAME;
    private final String PASSWORD;
    private final String SAVE_FILE_ADDRESS;
    private final String[] TAG_ARRAY;
    private final int N_TIME_NEXT;
    private final int DRIVER_NO;
    private ArrayList<String> followingList;
    private WebDriver driver;

    public InsDriver(SessionSettings sessionSettings) {
        this.USER_NAME = sessionSettings.userName;
        this.PASSWORD = sessionSettings.password;
        this.TAG_ARRAY = sessionSettings.tagArray;
        this.N_TIME_NEXT = sessionSettings.nTimeNext;
        this.DRIVER_NO = sessionSettings.driverNo;
        this.followingList=new ArrayList<>();
        this.SAVE_FILE_ADDRESS ="reports/all_following.csv";
    }

    public void initializeDriver(){
        if(DRIVER_NO==1){
            System.setProperty("webdriver.chrome.driver","drivers/chromedriver.exe");
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--lang=en-us");
            this.driver=new ChromeDriver(chromeOptions);
        }else if(DRIVER_NO==2){
            System.setProperty("webdriver.gecko.driver","drivers/geckodriver.exe");
            FirefoxOptions firefoxOptions= new FirefoxOptions();
            FirefoxProfile firefoxProfile =new FirefoxProfile();
            firefoxProfile.setPreference("intl.accept_languages","en-us");
            firefoxOptions.setProfile(firefoxProfile);
            this.driver=new FirefoxDriver(firefoxOptions);
        }else{
            System.setProperty("webdriver.edge.driver","drivers/msedgedriver.exe");
            this.driver=new EdgeDriver();
        }
        driver.manage().window().maximize();
    }
    public void loginStuff() throws InterruptedException {
        //goto ins login page
        driver.get("https://www.instagram.com/accounts/login/?hl=en");
        Thread.sleep(((int) (Math.random()*2000))+1000);
        //find area and send username
        //driver.findElement(By.name("username")).sendKeys(USER_NAME);
        enterTextRandomly(driver.findElement(By.name("username")), USER_NAME);
        Thread.sleep(((int) (Math.random()*2000))+1000);
        //find area and send password
        //driver.findElement(By.name("password")).sendKeys(PASSWORD);
        enterTextRandomly(driver.findElement(By.name("password")),PASSWORD);
        Thread.sleep(((int) (Math.random()*2000))+1000);
        //click login
        driver.findElement(By.cssSelector("#react-root > section > main > div > article > div > div:nth-child(1) > div > form > div:nth-child(4) > button")).click();
        //notification pass
        try{
            driver.findElement(By.cssSelector("body > div.RnEpo.Yx5HN > div > div > div.mt3GC > button.aOOlW.HoLwm'")).click();
        }catch (Exception e){
            Thread.sleep(10000);
        }
    }
    public void readFollowCsv(){
        try(Scanner scannerCsv=new Scanner(Paths.get(this.SAVE_FILE_ADDRESS))){
            while (scannerCsv.hasNextLine()){
                this.followingList.add(scannerCsv.nextLine().split(",")[1]);
            }
        }catch(Exception e){}
    }
    public void saveFollowedToCsv(){
        try{
            PrintWriter writer= new PrintWriter(this.SAVE_FILE_ADDRESS);
            for(int i=0;i<this.followingList.size();i++){
                writer.println((i+1)+","+this.followingList.get(i));
            }
            writer.close();
        } catch (Exception e) {}
    }
    public String commentAndFollow() throws InterruptedException {
        String tagUrl="https://www.instagram.com/explore/tags/";
        ArrayList<String> newFollowed=new ArrayList<>();
        int newFollowedCount=0;
        int likes=0;
        int comments=0;
        for (String tag:this.TAG_ARRAY) {
            //goto tag search result page
            driver.get(tagUrl+tag+"/");
            Thread.sleep(((int) (Math.random()*3000))+4000);
            //click first post
            driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/article/div[1]/div/div/div[1]/div[1]/a/div")).click();
            Thread.sleep(((int) (Math.random()*2000))+1000);
            //n time click next post and other stuff
            for(int i = 1; i<= N_TIME_NEXT; i++){
                try {
                    Thread.sleep(((int) (Math.random()*2000))+2000);
                    String otherUserName=driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/header/div[2]/div[1]/div[1]/a")).getText();
                    if(!this.followingList.contains(otherUserName)){
                        if(driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/header/div[2]/div[1]/div[2]/button")).getText().equals("Follow")){
                            driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/header/div[2]/div[1]/div[2]/button")).click();
                            //add newFollowed list and increase counter
                            newFollowedCount++;
                            newFollowed.add(otherUserName);
                            //like and comment randomly
                            driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/section[1]/span[1]/button")).click();
                            likes++;
                            Thread.sleep(((int) (Math.random()*9000))+18000);
                            int commentPossibility =(int)(Math.random()*11);
                            if(commentPossibility>7){
                                comments++;
                                WebElement commentBox=driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/section[3]/div/form/textarea"));
                                commentBox.click();
                                commentPossibility =(int)(Math.random()*11);
                                if(commentPossibility<7){
                                    commentBox.sendKeys("Really cool!");
                                }else if(commentPossibility<9){
                                    commentBox.sendKeys("Nice work :)");
                                }else if(commentPossibility==9){
                                    commentBox.sendKeys("Nice gallery!!");
                                }else{
                                    commentBox.sendKeys("So cool! :)");
                                }
                                Thread.sleep((int) (Math.random()*2000));
                                commentBox.sendKeys(Keys.ENTER);
                            }
                            Thread.sleep(((int) (Math.random()*6000))+22000);
                        }else {
                            Thread.sleep(((int) (Math.random()*6000))+20000);
                        }
                    }else {
                        Thread.sleep(((int) (Math.random()*6000))+20000);
                    }
                    driver.findElement(By.linkText("Next")).click();
                }catch (Exception e){
                    driver.findElement(By.linkText("Next")).click();
                    continue;
                }
            }
        }
        this.followingList.addAll(newFollowed);
        String results="New followed count: "+newFollowedCount+"\n"+
                "likes: "+likes+"\n"+
                "comments: "+comments+"\n"+
                "new followed users:"+"\n";
        for(String users:newFollowed){
            results+="\t"+users+"\n";
        }
        saveFollowedToCsv();
        return results;
    }
    public void closeDriver(){
        try{
            this.driver.close();
        }catch (Exception e){}
    }
    public void enterTextRandomly(WebElement enterArea, String enterText) throws InterruptedException {
        if (enterText.length()<5){
            enterArea.sendKeys(enterText); return;
        }
        int index=0;
        int step=(int)(Math.random()*5);
        enterArea.sendKeys(enterText.substring(index,step));
        Thread.sleep(500+(int)(Math.random()*500));
        enterTextRandomly(enterArea,enterText.substring(step));
    }
}
