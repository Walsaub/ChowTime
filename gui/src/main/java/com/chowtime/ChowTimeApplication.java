package com.chowtime;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChowTimeApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChowTimeApplication.class.getResource("database-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Chow Time");
        stage.setScene(scene);
        stage.show();
    }
}
