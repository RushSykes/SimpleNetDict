package Dict.Client;

import Dict.Server.DBConnector;
import javafx.application.Application;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.geometry.*;

/*
 * This is the main frame of the dictionary's UI
 * Applying JavaFX
 */

public class DictFrame extends Application {
    // Top-level scene
    private Scene welcomeScene;
    private Scene signUpScene;

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

    // One client, one dict frame
    private MainClient client = new MainClient();

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

        // =============================================================
        // Listener and handlers

        // When click login, perform signIn process
        logInButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Should look up the username in the database first

                int flag = client.logIn(newUserTextField.getText(), newPasswordField.getText());

                switch (flag) {
                    case 0:
                        userNotExist.setFill(Color.FORESTGREEN);
                        userNotExist.setText("Logged in");
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

        // When click signUp, switch to another scene and prompt for more info
        signUpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // First clear all the textfield
                userTextField.clear();
                passwordField.clear();
                userNotExist.setText("");

                // Assign it to the stage, whilst cancel teh assignment of welcome scene
                primaryStage.setScene(signUpScene);
            }
        });

        // Assign the scene to the stage
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
