module slotmachine {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    exports slotmachine to javafx.graphics;
    opens   slotmachine to javafx.fxml;
}
