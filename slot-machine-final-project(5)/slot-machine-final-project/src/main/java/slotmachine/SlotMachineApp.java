package slotmachine;

import javafx.application.Application;
import javafx.stage.Stage;

public class SlotMachineApp extends Application {

    private static Stage     primaryStage;
    private static UserStore userStore;

    //stage and store has to be static bc they need to be accessed from everywhere
    //we need them on every scene switch so we will pass them as arguments

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        userStore    = new UserStore();
        stage.setTitle("Slot Machine");
        stage.setResizable(false);
        showLogin();
        stage.show();
    }

    public static void showLogin() {
        primaryStage.setScene(new LoginView(primaryStage, userStore).buildScene());
    }

    public static void showRegister() {
        primaryStage.setScene(new RegisterView(primaryStage, userStore).buildScene());
    }

    public static void showGame(String username) {
        primaryStage.setScene(new GameView(primaryStage, username, userStore).buildScene());
    }

    public static void main(String[] args) {
        launch(args);
    }

    // we preferred this type of scene swtiches because i think fxml would be overkill, and would increse the complexity,
    // this architecture acts as central navigator for the entire application. Instead of letting each view handle its own scene transitions
}
