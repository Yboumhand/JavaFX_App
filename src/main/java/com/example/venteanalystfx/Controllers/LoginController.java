package com.example.venteanalystfx.Controllers;

import com.example.venteanalystfx.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public Label username_label;
    public TextField username_field;
    public Label password_label;
    public TextField password_field;
    public Button login_button;
    public Label error_label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_button.setOnAction(event -> onLogin());

        // event -> onLogin()
    }

    // Last modification

    // this method will be more complex at the end of the project because we'll handle dataBaseConnectivity
    private void onLogin() {
        // we can choose any node any label, we've choosen error_label as an example
        Stage stage = (Stage) error_label.getScene().getWindow(); // cast the window to Stage object

        // Evaluate User Login Credentials
        Model.getInstance().evaluateUserCredentials(username_field.getText(), password_field.getText());
        if (Model.getInstance().getUserClientLoginSuccessFlag()) {
            Model.getInstance().getViewFactory().showUserWindow();
            // Close the Login Stage
            Model.getInstance().getViewFactory().closeStage(stage);
        } else {
            username_field.setText("");
            password_field.setText("");
            error_label.setText("No Such Login Credentials.");
        }
    }
}
