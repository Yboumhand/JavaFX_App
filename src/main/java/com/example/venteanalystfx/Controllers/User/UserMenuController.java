package com.example.venteanalystfx.Controllers.User;

import com.example.venteanalystfx.Models.Model;
import com.example.venteanalystfx.Views.UserMenuOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;


// implementing the Initializable Interface

public class UserMenuController implements Initializable {
    public Button data_import_button;
    public Button data_processing_button;
    // public Button data_visualization_button;
    public Button user_interface_button;
    // public Button export_results_button;
    public Button logout_button;
    public Button report_button;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
    }

    private void addListeners() {
        data_import_button.setOnAction(event -> onImportData());
        data_processing_button.setOnAction(event -> onProcessData());
        // data_visualization_button.setOnAction(event -> onVisualizeResults());
        user_interface_button.setOnAction(event -> onUserInterface());
        // export_results_button.setOnAction(event -> onExportResults());
        logout_button.setOnAction(event -> onLogout());


    }

    private void onImportData() {
        Model.getInstance().getViewFactory().getUserSelectedMenuItem().set(UserMenuOptions.DATA_IMPORT);

    }

    private void onProcessData() {
        Model.getInstance().getViewFactory().getUserSelectedMenuItem().set(UserMenuOptions.DATA_PROCESS);
    }

   /* private void onVisualizeResults() {
        Model.getInstance().getViewFactory().getUserSelectedMenuItem().set(UserMenuOptions.VISUALIZE_RESULTS);
    }*/

    private void onUserInterface() {
        Model.getInstance().getViewFactory().getUserSelectedMenuItem().set(UserMenuOptions.USER_INTERFACE);
    }

    /*private void onExportResults() {
        Model.getInstance().getViewFactory().getUserSelectedMenuItem().set(UserMenuOptions.EXPORT_RESULTS);
    }*/

    private void onLogout() {
        Model.getInstance().setUserLoginSuccessFlag(false);
        // set the user attributes to null, since he's loged out the app
        Model.getInstance().getUser().firstNameProperty().set(null);
        Model.getInstance().getUser().lastNameProperty().set(null);
        Model.getInstance().getUser().userNameProperty().set(null);
        // Get the current stage
        Stage stage = (Stage) logout_button.getScene().getWindow();
        // Close it
        Model.getInstance().getViewFactory().closeStage(stage);
        // Show login window
        Model.getInstance().getViewFactory().showLoginWindow();
    }
}
