package slotmachine;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class RegisterView {

    private final Stage     stage;
    private final UserStore userStore;

    public RegisterView(Stage stage, UserStore userStore) {
        this.stage     = stage;
        this.userStore = userStore;
    }

    public Scene buildScene() {
        Label title = new Label("🎰  SLOT MACHINE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.GOLD);

        Label subtitle = new Label("Create Account");
        subtitle.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
        subtitle.setTextFill(Color.LIGHTGRAY);

        Label userLabel = new Label("Username");
        userLabel.setTextFill(Color.LIGHTGRAY);
        TextField userField = new TextField();
        userField.setPromptText("Choose a username");
        userField.setMaxWidth(260);

        Label passLabel = new Label("Password");
        passLabel.setTextFill(Color.LIGHTGRAY);
        PasswordField passField = new PasswordField();
        passField.setPromptText("Choose a password");
        passField.setMaxWidth(260);

        Label confirmLabel = new Label("Confirm Password");
        confirmLabel.setTextFill(Color.LIGHTGRAY);
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Repeat password");
        confirmField.setMaxWidth(260);

        //this here confirms password like any other real life password reset scenario

        Label messageLabel = new Label("");
        messageLabel.setFont(Font.font("Arial", 12));
        messageLabel.setTextFill(Color.SALMON);

        Button registerButton = new Button("Register");
        registerButton.setPrefWidth(260);
        registerButton.setPrefHeight(40);
        registerButton.setStyle(primaryButtonStyle());
        registerButton.setOnAction(e -> handleRegister(
                userField.getText().trim(),
                passField.getText(),
                confirmField.getText(),
                messageLabel
        ));

        Button backLink = new Button("Already have an account? Login");
        backLink.setStyle("-fx-background-color: transparent; -fx-text-fill: lightblue;" +
                          "-fx-cursor: hand; -fx-underline: true;");
        backLink.setOnAction(e -> SlotMachineApp.showLogin());

        //go back to login scene

        VBox root = new VBox(10,
                title, subtitle,
                userLabel, userField,
                passLabel, passField,
                confirmLabel, confirmField,
                messageLabel,
                registerButton, backLink
        );
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1a1a2e;");

        return new Scene(root, 400, 500);
    }

    private void handleRegister(String username, String password,
                                String confirm, Label messageLabel) {
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            messageLabel.setTextFill(Color.SALMON);
            return;

            //the returns here kills the method and let us try again, they dont work as continue, because the scene will
            //keep open during that time and automatically we use the method by typing the label area again.
        }
        if (username.length() < 3) {
            messageLabel.setText("Username must be at least 3 characters.");
            messageLabel.setTextFill(Color.SALMON);
            return;
        }
        if (password.length() < 4) {
            messageLabel.setText("Password must be at least 4 characters.");
            messageLabel.setTextFill(Color.SALMON);
            return;
            //this checks the password quality, we check only the number of characters but may be added '@!$%' symbols to check,
            //but the way it is is already works.
        }
        if (!password.equals(confirm)) {
            messageLabel.setText("Passwords do not match.");
            messageLabel.setTextFill(Color.SALMON);
            return;
        }
        try {
            boolean created = userStore.register(username, password);
            if (created) {
                messageLabel.setText("Account created! Redirecting...");
                messageLabel.setTextFill(Color.LIGHTGREEN);
                PauseTransition pause = new PauseTransition(Duration.seconds(1.0));
                pause.setOnFinished(e -> SlotMachineApp.showLogin());
                pause.play();
            } else {
                messageLabel.setText("Username already taken.");
                messageLabel.setTextFill(Color.SALMON);
                //this if else looks up to the users.txt via userStore.register and if username already taken, it throws false
                //if something goes wrong try interrupts and catch catches the error and writes the error with message label
            }
        } catch (IOException ex) {
            messageLabel.setText("Error: " + ex.getMessage());
            messageLabel.setTextFill(Color.SALMON);
        }
    }

    private String primaryButtonStyle() {
        return "-fx-background-color: goldenrod; -fx-text-fill: white;" +
               "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;";
    }
}
