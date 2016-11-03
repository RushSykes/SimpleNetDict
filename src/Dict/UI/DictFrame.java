package Dict.UI;

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
    // Top-level scene and pane
    Scene primaryScene;
    StackPane rootPane;

    // Widgets of welcomePane
    GridPane welcomePane;
    Text welcomeTitle;
    Label userName, password;
    TextField userTextField;
    PasswordField passwordField;
    HBox buttonHBox;
    Button logInButton, signUpButton;
    final Text userNotExist = new Text();
    // Widgets of dictPane
    // To be added...

    @Override
    public void start(Stage primaryStage) {
        /* Set properties of the window */
        primaryStage.setTitle("NeT Dictionary");
        primaryStage.show();

        /* Set up welcome Interface */
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
        buttonHBox = new HBox(10);
        signUpButton = new Button("Sign Up");
        logInButton = new Button("Log In");
        logInButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Should look up the username in the database first
                // To be added...
                userNotExist.setFill(Color.FIREBRICK);
                userNotExist.setText("User doesn't exist");
            }
        });

        buttonHBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonHBox.getChildren().addAll(signUpButton, logInButton);
        welcomePane.add(buttonHBox, 1, 4);

        /* Set up the rootPane (rootPane is used for switching between interfaces) */
        rootPane = new StackPane();
        rootPane.getChildren().addAll(welcomePane);

        // Add the rootPane to the primaryScene
        // so that the primary scene in fact has all the panes
        primaryScene = new Scene(rootPane, 480, 640);

        // Assign the scene to the stage
        primaryStage.setScene(primaryScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
