package com.example.venteanalystfx.Controllers.User;

import com.example.venteanalystfx.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    public BorderPane user_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().getUserSelectedMenuItem().addListener((observableValue, oldVal, newVal) -> {
            switch (newVal) {
                case DATA_PROCESS -> user_parent.setCenter(Model.getInstance().getViewFactory().getProcessDataView());
                // case VISUALIZE_RESULTS -> user_parent.setCenter(Model.getInstance().getViewFactory().getVisualizeResultsView());
                case USER_INTERFACE -> user_parent.setCenter(Model.getInstance().getViewFactory().getUserInterfaceView());
                // case EXPORT_RESULTS -> user_parent.setCenter(Model.getInstance().getViewFactory().getExportResultsView());
                default -> user_parent.setCenter(Model.getInstance().getViewFactory().getImportDataView());
            }
        });
    }
}
