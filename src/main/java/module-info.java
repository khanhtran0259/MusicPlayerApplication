module com.example.musicplayerapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires fontawesomefx;
    requires java.sql;


    opens com.example.musicplayerapp to javafx.fxml;
    exports com.example.musicplayerapp;
}