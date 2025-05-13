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

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.time.LocalDateTime;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

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
    // exportData button to pdf/ csv file implementation

    private void exportData() {
        // First check if there's data to export
        if (dbDataTable.getItems() == null || dbDataTable.getItems().isEmpty()) {
            dbStatusLabel.setText("No data to export");
            return;
        }

        // Create dialog to choose export format
        Alert formatDialog = new Alert(Alert.AlertType.CONFIRMATION);
        formatDialog.setTitle("Export Format");
        formatDialog.setHeaderText("Choose Export Format");
        formatDialog.setContentText("Select the format for exporting data:");

        ButtonType pdfButton = new ButtonType("PDF");
        ButtonType csvButton = new ButtonType("CSV");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        formatDialog.getButtonTypes().setAll(pdfButton, csvButton, cancelButton);

        // Show the dialog and get the result
        ButtonType result = formatDialog.showAndWait().orElse(cancelButton);

        if (result == cancelButton) {
            return; // User canceled the operation
        }

        // Set up file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Export File");

        // Set default file name with date
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String defaultFileName = "sales_export_" + dateStr;

        // Set extension based on chosen format
        if (result == pdfButton) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName(defaultFileName + ".pdf");
        } else {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            fileChooser.setInitialFileName(defaultFileName + ".csv");
        }

        // Show save dialog
        File file = fileChooser.showSaveDialog(mainTabPane.getScene().getWindow());

        if (file != null) {
            try {
                if (result == pdfButton) {
                    exportToPdf(file);
                } else {
                    exportToCsv(file);
                }
                dbStatusLabel.setText("Data exported successfully to: " + file.getAbsolutePath());
            } catch (Exception e) {
                dbStatusLabel.setText("Error exporting data: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Exports the data from the table to a CSV file
     */
    private void exportToCsv(File file) throws IOException {
        ObservableList<Sale> data = dbDataTable.getItems();
        try (FileWriter fw = new FileWriter(file)) {
            // Write header
            fw.write("ID,Date,Product,Category,Quantity,Unit Price,Total\n");

            // Write data rows
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Sale sale : data) {
                StringBuilder sb = new StringBuilder();
                sb.append(sale.idVenteProperty().get()).append(",");
                sb.append(sale.dateVenteProperty().get().format(dateFormatter)).append(",");
                sb.append(escapeSpecialCharacters(sale.produitProperty().get())).append(",");
                sb.append(escapeSpecialCharacters(sale.categorieProperty().get())).append(",");
                sb.append(sale.quantiteProperty().get()).append(",");
                sb.append(sale.prixUnitaireProperty().get()).append(",");
                sb.append(sale.totalProperty().get()).append("\n");
                fw.write(sb.toString());
            }
        }
    }

    /**
     * Helper method to escape special characters in CSV
     */
    private String escapeSpecialCharacters(String data) {
        if (data == null) {
            return "";
        }
        String escapedData = data.replaceAll("\"", "\"\"");
        // Wrap in quotes if the data contains special characters
        if (escapedData.contains(",") || escapedData.contains("\"") ||
                escapedData.contains("\n") || escapedData.contains("\r")) {
            escapedData = "\"" + escapedData + "\"";
        }
        return escapedData;
    }

    /**
     * Exports the data from the table to a PDF file
     */
    private void exportToPdf(File file) throws IOException {
        try {
            // Create Document object
            Document document = new Document();
            // Create PdfWriter instance
            PdfWriter.getInstance(document, new FileOutputStream(file));
            // Open the document
            document.open();

            // Add title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Sales Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Add space

            // Add date range information
            Font infoFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            String dateRange = "Period: " + startDatePicker.getValue() + " to " + endDatePicker.getValue();
            String categoryInfo = "Category: " + dbCategoryFilter.getValue();
            Paragraph dateInfo = new Paragraph(dateRange + " | " + categoryInfo, infoFont);
            dateInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(dateInfo);
            document.add(new Paragraph(" ")); // Add space

            // Create table
            PdfPTable pdfTable = new PdfPTable(7); // 7 columns
            pdfTable.setWidthPercentage(100);

            // Set column widths
            pdfTable.setWidths(new float[]{0.5f, 1.2f, 2f, 1.5f, 1f, 1.2f, 1.2f});

            // Add table headers
            String[] headers = {"ID", "Date", "Product", "Category", "Quantity", "Unit Price", "Total"};
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                pdfTable.addCell(cell);
            }

            // Add table data
            ObservableList<Sale> data = dbDataTable.getItems();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Sale sale : data) {
                pdfTable.addCell(String.valueOf(sale.idVenteProperty().get()));
                pdfTable.addCell(sale.dateVenteProperty().get().format(dateFormatter));
                pdfTable.addCell(sale.produitProperty().get());
                pdfTable.addCell(sale.categorieProperty().get());
                pdfTable.addCell(String.valueOf(sale.quantiteProperty().get()));
                pdfTable.addCell(String.valueOf(sale.prixUnitaireProperty().get()));
                pdfTable.addCell(String.valueOf(sale.totalProperty().get()));
            }

            // Add table to document
            document.add(pdfTable);

            // Add total
            document.add(new Paragraph(" ")); // Add space
            Font totalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Paragraph totalParagraph = new Paragraph("Total Sales: " + totalSalesLabel.getText(), totalFont);
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalParagraph);

            // Add footer with current date and time
            document.add(new Paragraph(" ")); // Add space
            Font footerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);
            Paragraph footer = new Paragraph("Generated on: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            // Close document
            document.close();
        } catch (DocumentException e) {
            throw new IOException("Error creating PDF: " + e.getMessage(), e);
        }
    }

    // Forms implementation

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
                    quantityField.clear();
                    saleStatusLabel.setText("The quantity must be greater than 0");
                    return;
                }
            } catch (NumberFormatException e) {
                quantityField.clear();
                saleStatusLabel.setText("Please enter a valid quantity");
                return;
            }

            float unitPrice;
            try {
                unitPrice = Float.parseFloat(priceField.getText());
                if (unitPrice <= 0) {
                    priceField.clear();
                    saleStatusLabel.setText("The unit price must be greater than 0");
                    return;
                }
            } catch (NumberFormatException e) {
                priceField.clear();
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
                clearSaleForm();
                saleStatusLabel.setText("Sale saved successfully");
            } else {
                saleStatusLabel.setText("Error while saving the sale");
            }

        } catch (Exception e) {
            saleStatusLabel.setText("An error has occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }


}