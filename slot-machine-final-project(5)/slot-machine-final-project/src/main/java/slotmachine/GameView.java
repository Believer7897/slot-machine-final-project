package slotmachine;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class GameView {

    private static final String[] SYMBOLS = {"🍒", "🍋", "🍇", "🔔", "7"};
    private static final String[] NAMES   = {"CHERRY", "LEMON", "GRAPE", "BELL", "SEVEN"};
    private static final int SEVEN = 4;
    private static final int BET   = 10;
    private static final int START = 100;

    private final Stage     stage;
    private final String    username;
    private final UserStore userStore;
    private int credit;

    private Label[] symbolLabel = new Label[3];
    private Label[] nameLabel   = new Label[3];
    private Label   creditLabel;
    private Label   resultLabel;
    private Button  spinButton;

    public GameView(Stage stage, String username, UserStore userStore) {
        this.stage     = stage;
        this.username  = username;
        this.userStore = userStore;

        try {
            this.credit = userStore.loadCredit(username, START);
        } catch (IOException e) {
            this.credit = START;
        }
        // this method looks for username data, if it found previous user it returns previous credit.
        // If it doesnt then user starts with default 100 credit
        // default 100 credit will apply only if the catch block runs. aka IOException triggers.
    }

    public Scene buildScene() {
        Label welcomeLabel = new Label("👤  " + username);
        welcomeLabel.setFont(Font.font("Arial", 13));
        welcomeLabel.setTextFill(Color.LIGHTGRAY);

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: salmon;" +
            "-fx-cursor: hand; -fx-underline: true;"
        );
        logoutButton.setOnAction(e -> {
            saveCredit();
            SlotMachineApp.showLogin();
        });

        //showslogin if pressed

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBar = new HBox(welcomeLabel, spacer, logoutButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 16, 6, 16));
        topBar.setStyle("-fx-background-color: #16213e;");

        Label title = new Label("🎰  SLOT MACHINE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.GOLD);

        creditLabel = new Label("Credits: " + credit);
        creditLabel.setFont(Font.font("Arial", 16));
        creditLabel.setTextFill(Color.WHITE);

        HBox reelRow = new HBox(14);
        reelRow.setAlignment(Pos.CENTER);

        for (int i = 0; i < 3; i++) {
            symbolLabel[i] = new Label("?");
            symbolLabel[i].setFont(Font.font("Arial", FontWeight.BOLD, 46));
            symbolLabel[i].setTextFill(Color.WHITE);
            symbolLabel[i].setMinWidth(90);
            symbolLabel[i].setAlignment(Pos.CENTER);

            nameLabel[i] = new Label("");
            nameLabel[i].setFont(Font.font("Arial", FontWeight.BOLD, 11));
            nameLabel[i].setTextFill(Color.GOLD);
            nameLabel[i].setAlignment(Pos.CENTER);

            VBox reel = new VBox(4, symbolLabel[i], nameLabel[i]);
            reel.setAlignment(Pos.CENTER);
            reel.setMinSize(120, 110);
            reel.setStyle(
                "-fx-background-color: #1e4a7a;" +
                "-fx-border-color: gold; -fx-border-width: 2;" +
                "-fx-border-radius: 10; -fx-background-radius: 10;"
            );
            reelRow.getChildren().add(reel);
        }

        spinButton = new Button("SPIN  (−" + BET + " credits)");
        spinButton.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        spinButton.setPrefSize(220, 46);
        spinButton.setStyle(
            "-fx-background-color: goldenrod; -fx-text-fill: white;" +
            "-fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;"
        );
        spinButton.setOnAction(e -> spin());

        //spin starts

        resultLabel = new Label("Press SPIN to play!");
        resultLabel.setFont(Font.font("Arial", 14));
        resultLabel.setTextFill(Color.LIGHTGRAY);

        Label hint = new Label("7-7-7 = +150   |   3 same = +80   |   2 same = +25");
        hint.setFont(Font.font("Arial", 11));
        hint.setTextFill(Color.GRAY);

        VBox center = new VBox(16, title, creditLabel, reelRow, spinButton, resultLabel, hint);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(20, 30, 30, 30));

        VBox root = new VBox(topBar, center);
        root.setStyle("-fx-background-color: #1a1a2e;");

        return new Scene(root, 430, 430);
    }

    private void spin() {
        if (credit < BET) {
            resultLabel.setText("No credits left!");
            resultLabel.setTextFill(Color.SALMON);
            return;
        }
        credit -= BET;
        creditLabel.setText("Credits: " + credit);
        spinButton.setDisable(true);
        resultLabel.setText("Spinning...");
        resultLabel.setTextFill(Color.LIGHTGRAY);

        int s1 = random(), s2 = random(), s3 = random();
        animateReel(0, 1.0, s1, null);
        animateReel(1, 1.5, s2, null);
        animateReel(2, 2.0, s3, () -> showResult(s1, s2, s3));
        //spinbutton disable true because we dont want to press it while on animation
        //this part is the key animation mechanic every wheel shown as timeparts 0.0-1.0, 1.0-1,5 , 1,5-2.0 ,
        //every symbol was already calculated the animation only makes excitement for use
    }

    //----------------------------------------------------------------------------------------------------

    private void animateReel(int index, double duration, int finalSymbol, Runnable onDone) {
        Timeline tl = new Timeline();
        int frames = (int)(duration / 0.07);
        //shows symbol per 0.07 second

        for (int i = 0; i < frames; i++) {
            int rand = random();
            tl.getKeyFrames().add(new KeyFrame(Duration.seconds(i * 0.07), e -> {
                symbolLabel[index].setText(SYMBOLS[rand]);
                symbolLabel[index].setTextFill(rand == SEVEN ? Color.RED : Color.WHITE);
                nameLabel[index].setText("");
                // here is manages FAKE symbols
            }));
        }

        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(duration), e -> {
            symbolLabel[index].setText(SYMBOLS[finalSymbol]);
            symbolLabel[index].setTextFill(finalSymbol == SEVEN ? Color.RED : Color.WHITE);
            nameLabel[index].setText(NAMES[finalSymbol]);
            if (onDone != null) onDone.run();
            //fake symbols appear by 0.07 timelines, when it is on 1.00 seconds fakes stop and real comes to the scene
            //
        }));

        tl.play();
        //timeline is a feature that makes 0.07 s keyframes to operate with each other and not to break.
    }

    //---------------------------------------------------------------------------------------------

    //this part gets s1 s2 s3 from animate reel block and calculates the profit


    private void showResult(int s1, int s2, int s3) {
        int reward;
        String message;
        Color color;

        if (s1 == s2 && s2 == s3 && s1 == SEVEN) {
            reward = 150; message = "🎉 JACKPOT! +150 credits"; color = Color.GOLD;
            playJackpot();
        } else if (s1 == s2 && s2 == s3) {
            reward = 80;  message = "✨ Three of a kind! +80 credits"; color = Color.LIGHTGREEN;
        } else if (s1 == s2 || s2 == s3 || s1 == s3) {
            reward = 25;  message = "👍 Two of a kind! +25 credits";   color = Color.LIGHTGREEN;
        } else {
            reward = 0;   message = "No match. Try again.";            color = Color.SALMON;
        }

        credit += reward;
        creditLabel.setText("Credits: " + credit);
        resultLabel.setText(message);
        resultLabel.setTextFill(color);

        saveCredit();

        spinButton.setDisable(credit < BET);
    }

    private void playJackpot() {
        for (int i = 0; i < 3; i++) {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), symbolLabel[i]);
            st.setFromX(1.0); st.setToX(1.3);
            st.setFromY(1.0); st.setToY(1.3);
            st.setCycleCount(4);
            st.setAutoReverse(true);
            st.play();
            //this is for only excitement too, this part makes symbol 1x and 1,3x and 1x again for 4 cycles
        }
    }

    private void saveCredit() {
        try {
            userStore.saveCredit(username, credit);
        } catch (IOException e) {
            System.err.println("Credit save failed: " + e.getMessage());
        }
    }

    private int random() {
        return (int)(Math.random() * SYMBOLS.length);
    }
}
