package com.example.venteanalystfx.Controllers.User;

import com.example.venteanalystfx.Models.Model;
import com.example.venteanalystfx.Models.Sale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class UserInterfaceController implements Initializable {

    // Tab 1: New Sale Form
    public TabPane mainTabPane;
    public TextField productField;
    public ComboBox<String> categoryComboBox;
    public String[] category = {"Informatique", "Electronique"};
    public TextField quantityField;
    public TextField priceField;
    public DatePicker saleDatePicker;
    public Button clearSaleButton;
    public Button saveSaleButton;
    public Label saleStatusLabel;

    // Tab 2: CSV Import
    public TextField csvFilePathField;
    public Button browseButton;
    public Button loadExampleButton;
    public TableView<Sale> csvPreviewTable;
    public CheckBox saveToDbCheckbox;
    public Button refreshCsvButton;
    public Button importCsvButton;
    public Label csvStatusLabel;

    // Tab 3: Database Data
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;
    public ComboBox<String> dbCategoryFilter;
    public Button loadDbButton;
    public TableView<Sale> dbDataTable;
    public TableColumn<Sale, Integer> idColumn;
    public TableColumn<Sale, LocalDate> dateColumn;
    public TableColumn<Sale, String> productColumn;
    public TableColumn<Sale, String> categoryColumn;
    public TableColumn<Sale, Integer> quantityColumn;
    public TableColumn<Sale, Float> priceColumn;
    public TableColumn<Sale, Float> totalColumn;
    public Label totalSalesLabel;
    public Button exportDataButton;
    public Label dbStatusLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TAB 1: NEW SALE FORM
        categoryComboBox.getItems().addAll(category);
        saleDatePicker.setValue(LocalDate.now());
        clearSaleButton.setOnAction(event -> clearSaleForm());
        saveSaleButton.setOnAction(event -> saveSale());

        // TAB 2: CSV Import
        browseButton.setOnAction(thisevent -> browseCSVFile());
        loadExampleButton.setOnAction(thisevent -> loadExampleCSV());
        importCsvButton.setOnAction(thisevent -> importCSVToDatabase());
        refreshCsvButton.setOnAction(event -> {
            if (!csvFilePathField.getText().isEmpty()) {
                loadCSVData(new File(csvFilePathField.getText()));
            } else {
                csvStatusLabel.setText("No CSV file selected to refresh.");
            }
        });

        // Configuration des colonnes de la table de prévisualisation CSV
        TableColumn<Sale, Integer> csvIdColumn = new TableColumn<>("ID");
        csvIdColumn.setCellValueFactory(cellData -> cellData.getValue().idVenteProperty().asObject());

        TableColumn<Sale, LocalDate> csvDateColumn = new TableColumn<>("Date");
        csvDateColumn.setCellValueFactory(cellData -> cellData.getValue().dateVenteProperty());
        csvDateColumn.setMinWidth(100);

        TableColumn<Sale, String> csvProductColumn = new TableColumn<>("Product");
        csvProductColumn.setCellValueFactory(cellData -> cellData.getValue().produitProperty());
        csvProductColumn.setMinWidth(150);

        TableColumn<Sale, String> csvCategoryColumn = new TableColumn<>("Category");
        csvCategoryColumn.setCellValueFactory(cellData -> cellData.getValue().categorieProperty());

        TableColumn<Sale, Integer> csvQuantityColumn = new TableColumn<>("Quantity");
        csvQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantiteProperty().asObject());

        TableColumn<Sale, Float> csvPriceColumn = new TableColumn<>("Unit Price");
        csvPriceColumn.setCellValueFactory(cellData -> cellData.getValue().prixUnitaireProperty().asObject());

        TableColumn<Sale, Float> csvTotalColumn = new TableColumn<>("Total");
        csvTotalColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty().asObject());

        csvPreviewTable.getColumns().addAll(csvIdColumn, csvDateColumn, csvProductColumn,
                csvCategoryColumn, csvQuantityColumn, csvPriceColumn, csvTotalColumn);

        // TAB 3: DATABASE DATA
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idVenteProperty().asObject());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateVenteProperty());
        productColumn.setCellValueFactory(cellData -> cellData.getValue().produitProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categorieProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantiteProperty().asObject());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().prixUnitaireProperty().asObject());
        totalColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty().asObject());

        startDatePicker.setValue(LocalDate.now().minusMonths(1));
        endDatePicker.setValue(LocalDate.now());
        dbCategoryFilter.getItems().addAll("All", category[0], category[1]);
        dbCategoryFilter.setValue("All");
        loadDbButton.setOnAction(event -> loadDatabaseData());
        exportDataButton.setOnAction(event -> exportData());

        loadDatabaseData(); // Charger les données initiales
    }

    private void browseCSVFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(mainTabPane.getScene().getWindow());
        if (selectedFile != null) {
            csvFilePathField.setText(selectedFile.getAbsolutePath());
            loadCSVData(selectedFile);
        }
    }

    private void loadExampleCSV() {
        File exampleFile = new File("exemple_ventes.csv");
        if (exampleFile.exists()) {
            loadCSVData(exampleFile);
            csvFilePathField.setText(exampleFile.getAbsolutePath());
        } else {
            csvStatusLabel.setText("Example CSV file not found.");
        }
    }

    private void loadCSVData(File csvFile) {
        ObservableList<Sale> salesFromCSV = FXCollections.observableArrayList();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] fields = line.split(",");
                if (fields.length == 7) {
                    try {
                        int idVente = Integer.parseInt(fields[0].trim());
                        LocalDate dateVente = LocalDate.parse(fields[1].trim(), dateFormatter);
                        String produit = fields[2].trim();
                        String categorie = fields[3].trim();
                        int quantite = Integer.parseInt(fields[4].trim());
                        float prixUnitaire = Float.parseFloat(fields[5].trim());
                        float total = Float.parseFloat(fields[6].trim());

                        salesFromCSV.add(new Sale(idVente, dateVente, produit, categorie, quantite, prixUnitaire, total));
                    } catch (NumberFormatException e) {
                        csvStatusLabel.setText("Error parsing numeric data in CSV.");
                        e.printStackTrace();
                        return;
                    } catch (java.time.format.DateTimeParseException e) {
                        csvStatusLabel.setText("Error parsing date in CSV.");
                        e.printStackTrace();
                        return;
                    }
                } else {
                    csvStatusLabel.setText("Invalid number of columns in CSV file.");
                    return;
                }
            }
            csvPreviewTable.setItems(salesFromCSV);
            csvStatusLabel.setText(salesFromCSV.size() + " records loaded from CSV.");
        } catch (IOException e) {
            csvStatusLabel.setText("Error reading CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void importCSVToDatabase() {
        if (saveToDbCheckbox.isSelected()) {
            ObservableList<Sale> salesToImport = csvPreviewTable.getItems();
            int importedCount = 0;
            for (Sale sale : salesToImport) {
                boolean success = Model.getInstance().addNewSale(
                        sale.produitProperty().get(),
                        sale.categorieProperty().get(),
                        sale.quantiteProperty().get(),
                        sale.prixUnitaireProperty().get(),
                        sale.dateVenteProperty().get()
                );
                if (success) {
                    importedCount++;
                } else {
                    csvStatusLabel.setText("Error importing some records.");
                    return;
                }
            }
            csvStatusLabel.setText(importedCount + " records imported successfully.");
            loadDatabaseData(); // Rafraîchir les données de la base de données après l'importation
        } else {
            csvStatusLabel.setText("Please check 'Save to database' to import data.");
        }
    }

    private void loadDatabaseData() {
        try {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String categoryFilter = dbCategoryFilter.getValue();

            if (startDate == null || endDate == null) {
                dbStatusLabel.setText("Please select valid dates");
                return;
            }

            if (startDate.isAfter(endDate)) {
                dbStatusLabel.setText("Start date must be before end date");
                return;
            }

            ObservableList<Sale> filteredData = Model.getInstance().getFilteredSells(startDate, endDate, categoryFilter);
            dbDataTable.setItems(filteredData);

            float totalSales = Model.getInstance().calculateTotalSales(startDate, endDate, categoryFilter);
            totalSalesLabel.setText(String.format("%.2f DH", totalSales));

            if (filteredData.isEmpty()) {
                dbStatusLabel.setText("No content in the table");
            } else {
                dbStatusLabel.setText(filteredData.size() + " record(s) found");
            }

        } catch (Exception e) {
            dbStatusLabel.setText("Error while loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void exportData() {
        dbStatusLabel.setText("Export functionality not yet implemented");
        // Implémentez ici la logique d'exportation des données
    }

    private void clearSaleForm() {
        productField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        quantityField.clear();
        priceField.clear();
        saleDatePicker.setValue(LocalDate.now());
        saleStatusLabel.setText("");
    }

    private void saveSale() {
        try {
            if (productField.getText().isEmpty()) {
                saleStatusLabel.setText("Please enter a product name");
                return;
            }

            if (categoryComboBox.getSelectionModel().isEmpty()) {
                saleStatusLabel.setText("Please select a category");
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityField.getText());
                if (quantity <= 0) {
                    saleStatusLabel.setText("The quantity must be greater than 0");
                    return;
                }
            } catch (NumberFormatException e) {
                saleStatusLabel.setText("Please enter a valid quantity");
                return;
            }

            float unitPrice;
            try {
                unitPrice = Float.parseFloat(priceField.getText());
                if (unitPrice <= 0) {
                    saleStatusLabel.setText("The unit price must be greater than 0");
                    return;
                }
            } catch (NumberFormatException e) {
                saleStatusLabel.setText("Please enter a valid unit price");
                return;
            }

            if (saleDatePicker.getValue() == null) {
                saleStatusLabel.setText("Please select a date");
                return;
            }

            String product = productField.getText();
            String category = categoryComboBox.getValue();
            LocalDate saleDate = saleDatePicker.getValue();

            boolean success = Model.getInstance().addNewSale(product, category, quantity, unitPrice, saleDate);

            if (success) {
                saleStatusLabel.setText("Sale saved successfully");
                clearSaleForm();
            } else {
                saleStatusLabel.setText("Error while saving the sale");
            }

        } catch (Exception e) {
            saleStatusLabel.setText("An error has occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}