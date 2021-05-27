module io.github.jezreal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;

    opens io.github.jezreal.controller to javafx.fxml;
    opens io.github.jezreal.model to javafx.base;

    exports io.github.jezreal.main;
    exports io.github.jezreal;
}
