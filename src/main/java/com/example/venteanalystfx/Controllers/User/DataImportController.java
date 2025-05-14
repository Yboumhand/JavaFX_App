package com.example.venteanalystfx.Controllers.User;


import com.example.venteanalystfx.Models.Model;
import com.example.venteanalystfx.Models.Sale;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;



public class DataImportController implements Initializable {

    @FXML
    public TableView<Sale> dataPreviewTable;
    @FXML
    public TableColumn<Sale, Integer> col1;
    @FXML
    public TableColumn<Sale, LocalDate> col2;
    @FXML
    public TableColumn<Sale, String> col3;
    @FXML
    public TableColumn<Sale, String> col4;
    @FXML
    public TableColumn<Sale, Integer> col5;
    @FXML
    public TableColumn<Sale, Float> col6;
    @FXML
    public TableColumn<Sale, Float> col7;

    public Button data_import_button;
    public Button refresh_button;
    public Label dbStatusLabel1;

    private final Model model = Model.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDataFromDatabase();
        configureTableColumns();
        addRefreshButtonListener();
    }

    private void loadDataFromDatabase() {
        ObservableList<Sale> sellsData = model.getAllSells();
        dataPreviewTable.setItems(sellsData);
        if (sellsData.isEmpty()) {
            dbStatusLabel1.setText("No content in the table");
        } else {
            dbStatusLabel1.setText(sellsData.size() + " record(s) found");
        }
    }

    private void configureTableColumns() {
        col1.setCellValueFactory(new PropertyValueFactory<>("idVente"));
        col2.setCellValueFactory(new PropertyValueFactory<>("dateVente"));
        col3.setCellValueFactory(new PropertyValueFactory<>("produit"));
        col4.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        col5.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        col6.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        col7.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    private void addRefreshButtonListener() {
        refresh_button.setOnAction(event -> handleRefreshButtonAction());
    }

    private void handleRefreshButtonAction() {
        loadDataFromDatabase();
    }
}
