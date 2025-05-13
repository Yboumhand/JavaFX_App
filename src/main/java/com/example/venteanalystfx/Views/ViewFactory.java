package com.example.venteanalystfx.Views;

import com.example.venteanalystfx.Controllers.User.UserController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;


public class ViewFactory {

    // User View Selection
    private final ObjectProperty<UserMenuOptions> userSelectedMenuItem;  // is a flag that changed based on what the user clicked on

    // Views
    private AnchorPane importDataView;
    private BorderPane processDataView;
    private VBox visualizeResultsView;
    private AnchorPane userInterfaceView;
    private AnchorPane exportResultsView;
    private AnchorPane logoutView;

    public ViewFactory() {
        this.userSelectedMenuItem = new SimpleObjectProperty<>();
    }

    public ObjectProperty<UserMenuOptions> getUserSelectedMenuItem() {
        return userSelectedMenuItem;
    }

    // Import Data View
    public AnchorPane getImportDataView() {
        if (importDataView == null) { // not loaded yet
            try {
                importDataView = new FXMLLoader(getClass().getResource("/Fxml/User/DataImport.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return importDataView;
    }

    // Process Data View
    public BorderPane getProcessDataView() {
        if (processDataView == null) { // not loaded yet
            try {
                processDataView = new FXMLLoader(getClass().getResource("/Fxml/User/DataProcess.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return processDataView;
    }

    // Visualize Results View
    public VBox getVisualizeResultsView() {
        if (visualizeResultsView == null) {
            try {
                visualizeResultsView = new FXMLLoader(getClass().getResource("/Fxml/User/VisualizeResults.fxml")).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return visualizeResultsView;
    }

    public AnchorPane getUserInterfaceView() {
        if (userInterfaceView == null) {
            try {
                userInterfaceView = new FXMLLoader(getClass().getResource("/Fxml/User/UserInterface.fxml")).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userInterfaceView;
    }

    // Export Results View
    public AnchorPane getExportResultsView() {
        if (exportResultsView == null) {
            try {
                exportResultsView = new FXMLLoader(getClass().getResource("/Fxml/User/ExportResults.fxml")).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return exportResultsView;
    }

    // Logout View
    public AnchorPane getLogoutView() {
        if (logoutView == null) {
            try {
                logoutView = new FXMLLoader(getClass().getResource("/Fxml/User/Logout.fxml")).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logoutView;
    }


    public void showLoginWindow () {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        createStage(loader);
    }

    public void showUserWindow () {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/User/User.fxml"));
        UserController userController = new UserController();
        loader.setController(userController);
        createStage(loader);
    }

    // DRY approach: since the code is duplicated many times,
    // so we tend to create a method to call it whenever we need this bunch of code

    private void createStage (FXMLLoader loader) {
        Scene scene = null;
        try {
            scene = new Scene(loader.load()); // incased inside a try-catch block
        } catch (Exception e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(scene); // add the scene to stage
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/Images/icon.png"))));
        stage.setResizable(false);
        stage.setTitle("Yassine App");
        stage.show();
    }

    public void closeStage(Stage stage) {
        stage.close();
    }

}
