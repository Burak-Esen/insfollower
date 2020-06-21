package insfollower;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainUI extends Application {
    private SessionSettings sessionSettings;
    private InsDriver insDriver;
    private VBox rootOfOptions=new VBox();
    private VBox rootOfDriver=new VBox();
    private Insets insetsForInnerHBox=new Insets(10,10,10,10);
    private Button toOptionsButton = new Button("to Options");
    private Button toDriverButton =new Button("to Driver");
    private HBox userNameHBox=new HBox();
    private Label userNameLabel=new Label("User Name:");
    private TextField userNameTextField=new TextField();
    private HBox passwordHBox= new HBox();
    private Label passwordLabel=new Label("Password:   ");
    private PasswordField passwordField=new PasswordField();
    private HBox tagHBox =new HBox();
    private Label tagLabel=new Label("Write tags\nhere only\nwith spacing");
    private TextArea tagTextArea=new TextArea();
    private HBox nTimeNextHBox=new HBox();
    private Label nTimeNextLabel=new Label("Look through\nfirst 'n'\nposts:");
    private TextField nTimeNextTextField=new TextField();
    private Label nTimeNextExampleLabel=new Label("example: 40");
    private HBox saveSetHBox=new HBox();
    private Button saveSettingsButton= new Button("Save Settings");
    private Label saveResult=new Label();
    private Button launchDriverButton=new Button("Launch Driver");
    private Label statusLabel = new Label("Click Launch Button for launch driver.\n" +
            "Please Close Driver with below Close button\n"+
            "Each post takes nearly 30-90 sec\n");
    Scene optionsScene = new Scene(rootOfOptions,400,500);
    Scene driverScene = new Scene(rootOfDriver,400,500);
    private TextArea resultsTextArea=new TextArea("Waiting to launch...");
    private Button closeDriverButton = new Button("Close Driver");
    private RadioButton chromeDriverRadio=new RadioButton("Chrome Driver");
    private RadioButton firefoxDriverRadio=new RadioButton("Firefox Driver");
    private RadioButton edgeDriverRadio=new RadioButton("Edge Driver");
    private ToggleGroup driverToggleGroup=new ToggleGroup();
    private Label driverVersionWarningLabel=new Label("!Driver runs based on version of your browser.\n!On some devices it may required to downlod suitable driver to\ndrivers folder.");
    //optional structure
    @Override
    public void init(){
        this.sessionSettings=new SessionSettings();
    }

    @Override
    public void stop(){
        if(insDriver!=null) {
            insDriver.closeDriver();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Ins. Follower App");
        //add scene switch buttons roots
        rootOfDriver.getChildren().add(toOptionsButton);
        rootOfOptions.getChildren().add(toDriverButton);

        //OptionsScene elements
            //--user name area
        userNameHBox.getChildren().addAll(userNameLabel,userNameTextField);

            //--User password area
        passwordHBox.getChildren().addAll(passwordLabel,passwordField);

            //--Tags area
        tagHBox.getChildren().addAll(tagLabel,tagTextArea);

            //--nTimeNext area
        nTimeNextHBox.getChildren().addAll(nTimeNextLabel,nTimeNextTextField,nTimeNextExampleLabel);

            //addAll nodes to rootOfOptions
        rootOfOptions.getChildren().addAll(userNameHBox, passwordHBox, tagHBox, nTimeNextHBox, chromeDriverRadio, firefoxDriverRadio,
                edgeDriverRadio, driverVersionWarningLabel, saveSetHBox);
        //driverScene elements addAll directly
        rootOfDriver.getChildren().addAll(launchDriverButton,statusLabel,closeDriverButton,resultsTextArea);

        setFeaturesOfNodes();
        setButtonsSetOnActions();

        toOptionsButton.setOnAction(actionEvent -> stage.setScene(optionsScene));
        toDriverButton.setOnAction(actionEvent -> stage.setScene(driverScene));
        stage.setScene(driverScene);
        stage.setX(50);
        stage.setY(50);
        stage.setResizable(false);
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    private void saveSetButtonHandler(){
        try{
            this.sessionSettings.userName=userNameTextField.getText();
            this.sessionSettings.password=passwordField.getText();
            this.sessionSettings.nTimeNext=Integer.valueOf((nTimeNextTextField.getText()));
            this.sessionSettings.tagArray=tagTextArea.getText().split(" ");
            this.sessionSettings.driverNo=(int)((RadioButton)driverToggleGroup.getSelectedToggle()).getUserData();
            sessionSettings.saveSettingsToLocal();
            saveResult.setText("Saved");
        }catch (Exception e){
            saveResult.setText("Save failure! Review the entries.");
        }
    }
    private void launchDriverHandler(TextArea results, Button closeButton) throws InterruptedException {
        this.insDriver = new InsDriver(sessionSettings);
        insDriver.initializeDriver();
        insDriver.readFollowCsv();
        insDriver.loginStuff();
        results.setText(insDriver.commentAndFollow());
        closeButton.setDisable(false);
    }
    private void setButtonsSetOnActions(){
        saveSettingsButton.setOnAction(actionEvent -> {
            saveSetButtonHandler();
        });

        closeDriverButton.setOnAction(actionEvent -> {
            this.insDriver.closeDriver();
            closeDriverButton.setDisable(true);
            launchDriverButton.setDisable(false);
        });

        launchDriverButton.setOnAction(actionEvent -> {
            resultsTextArea.setText("Results are waiting...");
            try {
                launchDriverButton.setDisable(true);
                launchDriverHandler(resultsTextArea, closeDriverButton);
            } catch (InterruptedException e) {
                launchDriverButton.setDisable(false);
                resultsTextArea.setText(e.getMessage()+"\n"+e.toString());
            }
        });
    }
    private void setFeaturesOfNodes(){
        resultsTextArea.setEditable(false);
        closeDriverButton.setDisable(true);
        VBox.setMargin(toOptionsButton, insetsForInnerHBox);
        VBox.setMargin(toDriverButton, insetsForInnerHBox);
        userNameTextField.setText(this.sessionSettings.userName);
        userNameHBox.setSpacing(5);
        userNameHBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(userNameHBox, insetsForInnerHBox);
        passwordField.setText(this.sessionSettings.password);
        passwordHBox.setSpacing(5);
        passwordHBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(passwordHBox, insetsForInnerHBox);
        tagTextArea.setText(String.join(" ",this.sessionSettings.tagArray));
        tagTextArea.setMaxSize(300,50);
        tagTextArea.setWrapText(true);
        tagHBox.setSpacing(5);
        tagHBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(tagHBox,insetsForInnerHBox);
        nTimeNextTextField.setText(String.valueOf(this.sessionSettings.nTimeNext));
        nTimeNextHBox.setSpacing(5);
        nTimeNextHBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(nTimeNextHBox, insetsForInnerHBox);
        saveSetHBox.getChildren().addAll(saveSettingsButton,saveResult);
        saveSetHBox.setSpacing(5);
        saveSetHBox.setAlignment(Pos.CENTER_LEFT);
        launchDriverButton.setPadding(new Insets(10,15,10,15));
        launchDriverButton.setBackground(new Background(new BackgroundFill(Color.rgb(220,157,157),null,null)));
        toOptionsButton.setPadding(new Insets(3,5,3,5));
        VBox.setMargin(saveSetHBox, insetsForInnerHBox);
        VBox.setMargin(launchDriverButton,insetsForInnerHBox);
        VBox.setMargin(statusLabel,insetsForInnerHBox);
        VBox.setMargin(closeDriverButton,insetsForInnerHBox);
        VBox.setMargin(resultsTextArea,insetsForInnerHBox);
        VBox.setMargin(driverVersionWarningLabel,insetsForInnerHBox);
        chromeDriverRadio.setToggleGroup(driverToggleGroup);
        firefoxDriverRadio.setToggleGroup(driverToggleGroup);
        edgeDriverRadio.setToggleGroup(driverToggleGroup);
        chromeDriverRadio.setUserData(1);
        firefoxDriverRadio.setUserData(2);
        edgeDriverRadio.setUserData(3);
        driverToggleGroup.selectToggle(selectDriverRadioWhenInitialize());
        VBox.setMargin(chromeDriverRadio, new Insets(10,0,0,10));
        VBox.setMargin(firefoxDriverRadio, new Insets(3,0,3,10));
        VBox.setMargin(edgeDriverRadio, new Insets(0,0,10,10));
        statusLabel.setBackground(new Background(new BackgroundFill(new Color(1,1,1,0.6),null,null)));
        statusLabel.setPadding(new Insets(0,5,0,5));
        //path based on nearest resources folder
        rootOfDriver.setBackground( new Background(new BackgroundImage(new Image("brick.jpg"), BackgroundRepeat.REPEAT,BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
    }
    private RadioButton selectDriverRadioWhenInitialize(){
        if((int)chromeDriverRadio.getUserData()==sessionSettings.driverNo){
            return chromeDriverRadio;
        }else if((int)firefoxDriverRadio.getUserData()==sessionSettings.driverNo){
            return firefoxDriverRadio;
        }
        return edgeDriverRadio;
    }
}
