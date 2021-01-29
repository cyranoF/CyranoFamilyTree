package org.cyrano;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;


/**
 * JavaFX FamilyTree program
 * by Cyrano Fischer
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("mainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        mainWindowController mainWindowController = fxmlLoader.getController();
        stage.setScene(scene);


        stage.setOnCloseRequest(e->{
            e.consume();
            if(mainWindowController.closeProgram()) {
                stage.close();
            }
        });

        ButtonType impBtn = new ButtonType("Import", ButtonBar.ButtonData.OK_DONE);
        ButtonType newPerBtn = new ButtonType("New Person", ButtonBar.ButtonData.NO);
        ButtonType close = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        final Alert importOrNew = new Alert(Alert.AlertType.CONFIRMATION,"Do you what to open a File or create a new one?",
                impBtn,newPerBtn, close);
        Optional<ButtonType> result = importOrNew.showAndWait();
        if (result.orElse(impBtn) == impBtn){
            mainWindowController.selectFile();
            stage.show();
        }
        else if (result.orElse(newPerBtn) == newPerBtn){
            mainWindowController.newStart();
            stage.show();
            }

    }


    public static void main(String[] args) {
        launch();
    }



}

