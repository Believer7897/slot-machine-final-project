package slotmachine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginView {

    private final Stage     stage;
    private final UserStore userStore;

    public LoginView(Stage stage, UserStore userStore) {
        this.stage     = stage;
        this.userStore = userStore;
    }

    public Scene buildScene() {
        Label title = new Label("🎰  SLOT MACHINE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.GOLD);

        Label subtitle = new Label("Login");
        subtitle.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
        subtitle.setTextFill(Color.LIGHTGRAY);

        Label userLabel = new Label("Username");
        userLabel.setTextFill(Color.LIGHTGRAY);
        TextField userField = new TextField();
        userField.setPromptText("Enter username");
        userField.setMaxWidth(260);

        Label passLabel = new Label("Password");
        passLabel.setTextFill(Color.LIGHTGRAY);
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter password");
        passField.setMaxWidth(260);

        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.SALMON);
        errorLabel.setFont(Font.font("Arial", 12));

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(260);
        loginButton.setPrefHeight(40);
        loginButton.setStyle(primaryButtonStyle());
        loginButton.setOnAction(e -> handleLogin(
                userField.getText().trim(),
                passField.getText(),
                errorLabel
        ));

        Button registerLink = new Button("Don't have an account? Register");
        registerLink.setStyle("-fx-background-color: transparent; -fx-text-fill: lightblue;" +
                              "-fx-cursor: hand; -fx-underline: true;");
        registerLink.setOnAction(e -> SlotMachineApp.showRegister());

        //highlightes button as link and on action returns showRegister method;

        VBox root = new VBox(10,
                title, subtitle,
                userLabel, userField,
                passLabel, passField,
                errorLabel,
                loginButton, registerLink
        );
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1a1a2e;");

        return new Scene(root, 400, 420);
    }

    //-------------------------------------------------------------------------------------
    //this is the main logic part of this class
    private void handleLogin(String username, String password, Label errorLabel) {
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
            //checks if is empty or not
        }
        try {
            if (userStore.login(username, password)) {
                SlotMachineApp.showGame(username);
                //this method checks if username is valid and again checks if username and password equals
            } else {
                errorLabel.setText("Wrong username or password.");
            }
        } catch (IOException ex) {
            errorLabel.setText("Error: " + ex.getMessage());
            //here is a default ioexception structure, if something doesnt go right then userStore.login throws io exception
            //and our error label shows error message
            //everything works syncronized, for example here we use userStore to comminucate with not fxml
        }
    }
    //--------------------------------------------------------------------------------------

    private String primaryButtonStyle() {
        return "-fx-background-color: goldenrod; -fx-text-fill: white;" +
               "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;";
    }
}
