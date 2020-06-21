package insfollower;

import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Scanner;

public class SessionSettings {
    private final String SETTINGS_FILE_ADDRESS;
    public String userName;
    public String password;
    public String[] tagArray;
    public int nTimeNext;
    public int driverNo;

    public SessionSettings() {
        SETTINGS_FILE_ADDRESS = "settings.usersettings";
        getSettingsFromLocal();
    }
    private void getSettingsFromLocal(){
        try(Scanner scannerFile=new Scanner(Paths.get(SETTINGS_FILE_ADDRESS))){
            this.userName=scannerFile.nextLine();
            this.password=scannerFile.nextLine();
            this.nTimeNext=Integer.valueOf(scannerFile.nextLine());
            this.tagArray=scannerFile.nextLine().split(" ");
            this.driverNo=Integer.valueOf(scannerFile.nextLine());
        }catch(Exception e){}
    }
    public void saveSettingsToLocal(){
        try{
            PrintWriter writer= new PrintWriter("settings.usersettings");
            writer.println(this.userName);
            writer.println(this.password);
            writer.println(this.nTimeNext);
            writer.println(String.join(" ",this.tagArray));
            writer.print(this.driverNo);
            writer.close();
        } catch (Exception e) {}
    }
}
