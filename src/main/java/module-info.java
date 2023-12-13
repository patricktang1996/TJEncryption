module com.example.tjencryption {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.tjencryption to javafx.fxml;
    exports com.example.tjencryption;
}