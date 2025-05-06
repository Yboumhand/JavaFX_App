package com.example.venteanalystfx.Controllers.User;


import com.example.venteanalystfx.Models.Model;
import com.example.venteanalystfx.Models.Sell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;



public class DataImportController implements Initializable {

    public TextField csvPathField;
    public Button browseButton;
    public Button loadExampleButton;
    @FXML
    public TableView<Sell> dataPreviewTable;
    @FXML
    public TableColumn<ObservableList<String>, String> col1;
    @FXML
    public TableColumn<ObservableList<String>, String> col2;
    @FXML
    public TableColumn<ObservableList<String>, String> col3;
    @FXML
    public TableColumn<ObservableList<String>, String> col4;
    @FXML
    public TableColumn<ObservableList<String>, String> col5;
    @FXML
    public TableColumn<ObservableList<String>, String> col6;
    @FXML
    public TableColumn<ObservableList<String>, String> col7;
    public CheckBox saveToDbCheckbox;
    public Button data_import_button;

    private final Model model = Model.getInstance();
    private ObservableList<Sell> importedDataFromCSV = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDataFromDatabase();
        configureTableColumns();
    }

    private void loadDataFromDatabase() {
        ObservableList<Sell> sellsData = model.getAllSells();
        dataPreviewTable.setItems(sellsData);
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

//    @FXML
//    private void handleLoadExampleButtonAction() {
//        importedDataFromCSV.clear(); // Effacer les données précédemment chargées du CSV
//
//        try (InputStream inputStream = getClass().getResourceAsStream("/exemple_ventes.csv")) {
//            assert inputStream != null;
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//
//                String line;
//                boolean isFirstLine = true; // to ignore the first row (columnsName)
//
//                while ((line = reader.readLine()) != null) {
//                    if (isFirstLine) {
//                        isFirstLine = false;
//                        continue; //ignore the first row
//                    }
//
//                    String[] fields = line.split(","); // for columns seperator
//
//                    if (fields.length == 7) {
//                        try {
//                            int idVente = Integer.parseInt(fields[0].trim());
//                            LocalDate dateVente = LocalDate.parse(fields[1].trim());
//                            String produit = fields[2].trim();
//                            String categorie = fields[3].trim();
//                            int quantite = Integer.parseInt(fields[4].trim());
//                            float prixUnitaire = Float.parseFloat(fields[5].trim());
//                            float total = Float.parseFloat(fields[6].trim());
//
//                            importedDataFromCSV.add(new Sell(idVente, dateVente, produit, categorie, quantite, prixUnitaire, total));
//                        } catch (NumberFormatException e) {
//                            System.err.println("Erreur de format dans la ligne CSV : " + line + " - " + e.getMessage());
//                            // handle the message error
//                        }
//                    } else {
//                        System.err.println("Ligne CSV invalide (nombre de colonnes incorrect) : " + line);
//                        // handle the error
//                    }
//                }
//
//                dataPreviewTable.setItems(importedDataFromCSV); // display csv_data within the TableView
//
//            }
//        } catch (IOException e) {
//            System.err.println("Erreur lors de la lecture du fichier d'exemple : " + e.getMessage());
//            // Gérer l'erreur (afficher un message à l'utilisateur)
//        }
//    }




}
