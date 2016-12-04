package UI;

import ADT.UserInfo;
import Client.MainClient;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.geometry.*;

import java.util.ArrayList;

/*
 * This is the main frame of the dictionary's UI
 * Applying JavaFX
 */

public class DictFrame extends Application {
    // Top-level scene
    private Scene welcomeScene;
    private Scene signUpScene;
    private Scene searchScene;

    // Widgets of welcomeScene
    private GridPane welcomePane;
    private Text welcomeTitle;
    private Label userName, password;
    private TextField userTextField;
    private PasswordField passwordField;
    private HBox welcomeButtonHBox;
    private Button logInButton, signUpButton;
    final private Text userNotExist = new Text();

    // Widgets of signUpScene
    private GridPane signUpPane;
    private Text signUpTitle;
    private Label newUser, newPassword, newConfirm;
    private TextField newUserTextField;
    private PasswordField newPasswordField, confirmField;
    private HBox regButtonHBox;
    private Button registerButton, backButton;
    final private Text regInfo = new Text();

    // Widgets of searchScene
    private GridPane searchPane;
    private Label searchWord;
    private TextField searchWordTextField;
    private Button searchButton;
    private TextArea fromYoudao, fromBing, fromJinshan;
    private ToggleButton likeYoudao, likeBing, likeJinshan;
    private CheckBox checkYoudao, checkBing, checkJinshan;
    private boolean[] searchFlag;
    private ListView<String> currentUser;

    // One client, one dict frame
    private MainClient client = new MainClient();
    private String clientUser;

    // User list real-time handler
    class UserList extends Thread {
        public void run() {
            try {
                while(true) {
                    sleep(5000);
                    ArrayList<String>onlineUsers = new ArrayList<>();
                    onlineUsers = client.currentUserInfo();
                    // TODO: refresh current user list
                }

            }
            catch(InterruptedException ex) {

            }
        }
    }

    // Search button single-time thread test
    class Search extends Thread {
        public void run() {
            String word = searchWordTextField.getText();
                /* TODO: Perhaps we need to check if the input is valid first
                later...
                */
            if (onlyOne()) {
                UserInfo result;
                if (searchFlag[0]) result = client.query(word, 0); // Youdao
                else if (searchFlag[1]) result = client.query(word, 1); // Bing
                else result = client.query(word, 2); // Jinshan

                switch (result.getQueryType()) {
                    // 0 for Youdao
                    case 0:
                        fromYoudao.setText(result.getResult());
                        break;
                    // 1 for Bing
                    case 1:
                        fromBing.setText(result.getResult());
                        break;
                    // 2 for Jinshan
                    case 2:
                        fromJinshan.setText(result.getResult());
                        break;
                    default:
                }
            }

            // TODO: Results need to be sorted
            else if (all()) {
                UserInfo[] result;
                result = client.queryAll(word);
                fromYoudao.setText(result[0].getResult());
                fromBing.setText(result[1].getResult());
                fromJinshan.setText(result[2].getResult());
            }
            // TODO: Results need to be sorted
            else {
                for (int i = 0; i < searchFlag.length; i++) {
                    if (searchFlag[i]) {
                        UserInfo result;
                        result = client.query(word, i);
                    }
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        /* Set properties of the window */
        primaryStage.setTitle("NeT Dictionary");

        /* Set up welcome Interface */
        // ==============================================================
        // welcomePane
        welcomePane = new GridPane();
        welcomePane.setAlignment(Pos.CENTER);
        welcomePane.setHgap(10);
        welcomePane.setVgap(10);
        welcomePane.setPadding(new Insets(25, 25, 25, 25));

        // welcomeTitle
        welcomeTitle = new Text("Welcome\nto NeT Dictionary");
        welcomeTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        welcomePane.add(welcomeTitle, 0, 0, 2, 1);

        // User name and password
        userName = new Label("User Name:");
        welcomePane.add(userName, 0, 1);

        userTextField = new TextField();
        welcomePane.add(userTextField, 1, 1);

        password = new Label("Password:");
        welcomePane.add(password, 0, 2);

        passwordField = new PasswordField();
        welcomePane.add(passwordField, 1, 2);
        welcomePane.add(userNotExist, 1, 6);

        // Buttons
        welcomeButtonHBox = new HBox(10);
        signUpButton = new Button("Sign Up");
        logInButton = new Button("Log In");

        welcomeButtonHBox.setAlignment(Pos.BOTTOM_RIGHT);
        welcomeButtonHBox.getChildren().addAll(signUpButton, logInButton);
        welcomePane.add(welcomeButtonHBox, 1, 4);

        welcomeScene = new Scene(welcomePane, 480, 640);

        // ===============================================================
        // SignUp scene
        signUpPane = new GridPane();
        signUpPane.setAlignment(Pos.CENTER);
        signUpPane.setHgap(10);
        signUpPane.setVgap(10);
        signUpPane.setPadding(new Insets(25, 25, 25, 25));

        // signUpTitle
        signUpTitle = new Text("Sign Up\nHere");
        signUpTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        signUpPane.add(signUpTitle, 0, 0, 2, 1);

        /*
          Label newUser, newPassword, newConfirm;
          TextField newUserTextField;
          PasswordField newPasswordField, confirmField;
          HBox regButtonHBox;
          Button registerButton, backButton;
          final Text regInfo = new Text();
        */
        // New User name and password
        newUser = new Label("New user Name:");
        signUpPane.add(newUser, 0, 1);

        newUserTextField = new TextField();
        signUpPane.add(newUserTextField, 1, 1);

        newPassword = new Label("Password:");
        signUpPane.add(newPassword, 0, 2);

        newPasswordField = new PasswordField();
        signUpPane.add(newPasswordField, 1, 2);

        newConfirm = new Label("Confirm password:");
        signUpPane.add(newConfirm, 0, 3);

        confirmField = new PasswordField();
        signUpPane.add(confirmField, 1, 3);
        signUpPane.add(regInfo, 1, 7);

        regButtonHBox = new HBox(10);
        backButton = new Button("Back");
        registerButton = new Button("Register!");

        regButtonHBox.setAlignment(Pos.BOTTOM_RIGHT);
        regButtonHBox.getChildren().addAll(backButton, registerButton);
        signUpPane.add(regButtonHBox, 1, 5);

        signUpScene = new Scene(signUpPane, 480, 640);

        // ===============================================================
        // Search scene
        searchPane = new GridPane();
        searchPane.setAlignment(Pos.CENTER);
        searchPane.setHgap(10);
        searchPane.setVgap(10);
        searchPane.setPadding(new Insets(25, 25, 25, 25));

        /*
          Label searchWord;
          TextField searchWordTextField;
          Button searchButton;
          ListView currentUser;
          CheckBox checkJinshan, checkYoudao, checkBing;
          ToggleButton likeJinshan, likeYoudao, likeBing;
         */
        searchWord = new Label("Search:");
        searchPane.add(searchWord, 0, 0);

        searchWordTextField = new TextField();
        GridPane.setColumnSpan(searchWordTextField, 2);
        searchPane.add(searchWordTextField, 1, 0);

        searchButton = new Button("Search");
        searchPane.add(searchButton, 3, 0);

        currentUser = new ListView<>();
        GridPane.setRowSpan(currentUser, 3);
        searchPane.add(currentUser, 0, 1);

        // Checkboxes
        checkYoudao = new CheckBox("Youdao");
        searchPane.add(checkYoudao, 1, 1);
        checkBing = new CheckBox("Bing");
        searchPane.add(checkBing, 1, 2);
        checkJinshan = new CheckBox("Jinshan");
        searchPane.add(checkJinshan, 1, 3);

        searchFlag = new boolean[3];
        searchFlag[0] = false;
        searchFlag[1] = false;
        searchFlag[2] = false;

        // Text areas
        fromYoudao = new TextArea();
        searchPane.add(fromYoudao, 2, 1);
        fromBing = new TextArea();
        searchPane.add(fromBing, 2, 2);
        fromJinshan = new TextArea();
        searchPane.add(fromJinshan, 2, 3);

        // Like buttons
        likeYoudao = new ToggleButton("like it");
        searchPane.add(likeYoudao, 3, 1);
        likeBing = new ToggleButton("like it");
        searchPane.add(likeBing, 3, 2);
        likeJinshan = new ToggleButton("like it");
        searchPane.add(likeJinshan, 3, 3);

        searchScene = new Scene(searchPane, 1200, 800);

        // =============================================================
        // Listener and handlers

        // When click login, perform signIn process
        logInButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Should look up the username in the database first

                int flag = client.logIn(userTextField.getText(), passwordField.getText());

                switch (flag) {
                    case 0:
                        clientUser = userTextField.getText();
                        userNotExist.setFill(Color.FORESTGREEN);
                        userNotExist.setText("Logged in");
                        userTextField.clear();
                        passwordField.clear();
                        userNotExist.setText("");
                        primaryStage.setScene(searchScene);
                        break;
                    case 1:
                        userNotExist.setFill(Color.FIREBRICK);
                        userNotExist.setText("Wrong password");
                        break;
                    case 2:
                        userNotExist.setFill(Color.FIREBRICK);
                        userNotExist.setText("User doesn't exist");
                        break;
                    default:
                        userNotExist.setFill(Color.FIREBRICK);
                        userNotExist.setText("An error occurred");
                }
            }
        });

        // SignUp scene buttons below
        // When click back, go back to welcome scene
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // First clear all the fields
                newUserTextField.clear();
                newPasswordField.clear();
                confirmField.clear();
                primaryStage.setScene(welcomeScene);
            }
        });

        // When click register, perform the register process
        registerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Should look up the username in the database first
                // To be added...
                int flag = client.signUp(newUserTextField.getText(), newPasswordField.getText(), confirmField.getText());
                // -1 for exception
                // 0 for success
                // 1 for password mismatching
                // 2 for user exists
                switch (flag) {
                    case 0:
                        regInfo.setFill(Color.FORESTGREEN);
                        regInfo.setText("Register successful");
                        break;
                    case 1:
                        regInfo.setFill(Color.FIREBRICK);
                        regInfo.setText("Password mismatching");
                        break;
                    case 2:
                        regInfo.setFill(Color.FIREBRICK);
                        regInfo.setText("User exists");
                        break;
                    default:
                        regInfo.setFill(Color.FIREBRICK);
                        regInfo.setText("An error occurred");
                }
            }
        });

        // When click signUp, switch to signUp scene and prompt for more info
        signUpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // First clear all the textfield
                userTextField.clear();
                passwordField.clear();
                userNotExist.setText("");

                // Assign it to the stage, whilst cancel the assignment of welcome scene
                primaryStage.setScene(signUpScene);
            }
        });

        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Search search_thread = new Search();
                search_thread.start();
                /* TODO: Perhaps we need to check if the input is valid first
                later...
                */
                /*
                String word = searchWordTextField.getText();
                if(onlyOne()) {
                    UserInfo result;
                    if(searchFlag[0]) result = client.query(word, 0); // Youdao
                    else if(searchFlag[1]) result = client.query(word, 1); // Bing
                    else result = client.query(word, 2); // Jinshan

                    switch (result.getQueryType()) {
                        // 0 for Youdao
                        case 0:
                            fromYoudao.setText(result.getResult());
                            break;
                        // 1 for Bing
                        case 1:
                            fromBing.setText(result.getResult());
                            break;
                        // 2 for Jinshan
                        case 2:
                            fromJinshan.setText(result.getResult());
                            break;
                        default:
                    }
                }

                // TODO: Results need to be sorted
                else if(all()) {
                    UserInfo[] result;
                    result = client.queryAll(word);
                    fromYoudao.setText(result[0].getResult());
                    fromBing.setText(result[1].getResult());
                    fromJinshan.setText(result[2].getResult());
                }
                // TODO: Results need to be sorted
                else {
                    for(int i = 0; i < searchFlag.length; i++) {
                        if(searchFlag[i]) {
                            UserInfo result;
                            result = client.query(word, i);
                        }
                    }
                }*/
            }
        });

        checkYoudao.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue == true)
                    searchFlag[0] = true;
                else
                    searchFlag[0] = false;
            }
        });

        likeYoudao.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserInfo result;
                result = client.likeIt(searchWordTextField.getText(), clientUser, 0);
                // A like this time
                if(result.getLiked()) {
                    likeYoudao.setSelected(true);
                    likeYoudao.setText("liked");
                }
                // A dislike this time
                else {
                    likeYoudao.setSelected(false);
                    likeYoudao.setText("like it");
                }
            }
        });


        checkBing.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue == true)
                    searchFlag[1] = true;
                else
                    searchFlag[1] = false;
            }
        });

        likeBing.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserInfo result;
                result = client.likeIt(searchWordTextField.getText(), clientUser, 1);
                // A like this time
                if(result.getLiked()) {
                    likeBing.setSelected(true);
                    likeBing.setText("liked");
                }
                // A dislike this time
                else {
                    likeBing.setSelected(false);
                    likeBing.setText("like it");
                }
            }
        });

        checkJinshan.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue == true)
                    searchFlag[2] = true;
                else
                    searchFlag[2] = false;
            }
        });

        likeJinshan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserInfo result;
                result = client.likeIt(searchWordTextField.getText(), clientUser, 2);
                // A like this time
                if(result.getLiked()) {
                    likeJinshan.setSelected(true);
                    likeJinshan.setText("liked");
                }
                // A dislike this time
                else {
                    likeJinshan.setSelected(false);
                    likeJinshan.setText("like it");
                }
            }
        });

        // Assign the scene to the stage
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private boolean onlyOne() {
        if(
                (searchFlag[0] && !searchFlag[1] && !searchFlag[2]) ||
                (!searchFlag[0] && searchFlag[1] && !searchFlag[2]) ||
                 (!searchFlag[0] && !searchFlag[1] && searchFlag[2])
        ) {
            return true;
        }
        return false;
    }

    private boolean all() {
        if(                (searchFlag[0] && searchFlag[1] && searchFlag[2]) ||
                (!searchFlag[0] && !searchFlag[1] && !searchFlag[2])
                ) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
