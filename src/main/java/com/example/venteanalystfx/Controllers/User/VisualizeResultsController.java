//package com.example.venteanalystfx.Controllers.User;
//
//import com.example.venteanalystfx.Models.Model;
//import com.example.venteanalystfx.Models.Sale;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.pdf.PdfWriter;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.chart.*;
//import javafx.scene.control.*;
//import javafx.scene.layout.AnchorPane;
//import javafx.stage.FileChooser;
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartUtils;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.data.category.DefaultCategoryDataset;
//import org.jfree.data.general.DefaultPieDataset;
//
//import java.io.*;
//import java.net.URL;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.ResourceBundle;
//
//public class VisualizeResultsController implements Initializable {
//
//    // Interface components
//    @FXML
//    private DatePicker startDatePicker;
//    @FXML
//    private DatePicker endDatePicker;
//    @FXML
//    private ComboBox<String> categoryComboBox;
//    @FXML
//    private Button applyFilterBtn;
//    @FXML
//    private Button resetFilterBtn;
//    @FXML
//    private TabPane chartsTabPane;
//    @FXML
//    private AnchorPane piechartContainer;
//    @FXML
//    private AnchorPane lineChartContainer;
//    @FXML
//    private AnchorPane barChartContainer;
//    @FXML
//    private Button exportCategoryChartBtn;
//    @FXML
//    private Button exportCategoryDataBtn;
//    @FXML
//    private Button exportTrendsChartBtn;
//    @FXML
//    private Button exportTrendsDataBtn;
//    @FXML
//    private Button exportTopProductsChartBtn;
//    @FXML
//    private Button exportTopProductsDataBtn;
//    @FXML
//    private Label statusLabel;
//
//    // JavaFX Charts
//    private PieChart categorySalesPieChart;
//    private LineChart<String, Number> monthlySalesLineChart;
//    private BarChart<String, Number> topProductsBarChart;
//
//    // Reference to the main model
//    private final Model model = Model.getInstance();
//
//    // Data storage
//    private ObservableList<Sale> filteredSales;
//    private ObservableList<String[]> categoryData;
//    private ObservableList<String[]> monthlyData;
//    private ObservableList<String[]> topProductsData;
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        // Initialize date pickers with default values
//        startDatePicker.setValue(LocalDate.now().minusMonths(6));
//        endDatePicker.setValue(LocalDate.now());
//
//        // Initialize category combo box
//        populateCategoryComboBox();
//
//        // Setup charts
//        setupCategorySalesPieChart();
//        setupMonthlySalesLineChart();
//        setupTopProductsBarChart();
//
//        // Set up button action handlers
//        setupButtonActions();
//
//        // Load initial data based on default filter settings
//        applyFilters();
//
//        // Status update
//        updateStatus("Charts initialized successfully");
//    }
//
//    private void populateCategoryComboBox() {
//        try {
//            ObservableList<String> categories = model.getAllCategories();
//            categoryComboBox.setItems(categories);
//            categoryComboBox.getSelectionModel().selectFirst();
//        } catch (Exception e) {
//            e.printStackTrace();
//            updateStatus("Error loading categories: " + e.getMessage());
//        }
//    }
//
//    private void setupCategorySalesPieChart() {
//        categorySalesPieChart = new PieChart();
//        categorySalesPieChart.setTitle("Sales by Category");
//        categorySalesPieChart.setLabelsVisible(true);
//        categorySalesPieChart.setLegendVisible(true);
//
//        AnchorPane.setTopAnchor(categorySalesPieChart, 0.0);
//        AnchorPane.setRightAnchor(categorySalesPieChart, 0.0);
//        AnchorPane.setBottomAnchor(categorySalesPieChart, 0.0);
//        AnchorPane.setLeftAnchor(categorySalesPieChart, 0.0);
//
//        piechartContainer.getChildren().add(categorySalesPieChart);
//    }
//
//    private void setupMonthlySalesLineChart() {
//        // Define axis
//        CategoryAxis xAxis = new CategoryAxis();
//        NumberAxis yAxis = new NumberAxis();
//        xAxis.setLabel("Month");
//        yAxis.setLabel("Amount (DH)");
//
//        // Create chart
//        monthlySalesLineChart = new LineChart<>(xAxis, yAxis);
//        monthlySalesLineChart.setTitle("Monthly Sales Trends");
//        monthlySalesLineChart.setCreateSymbols(true);
//        monthlySalesLineChart.setAnimated(false);
//
//        AnchorPane.setTopAnchor(monthlySalesLineChart, 0.0);
//        AnchorPane.setRightAnchor(monthlySalesLineChart, 0.0);
//        AnchorPane.setBottomAnchor(monthlySalesLineChart, 0.0);
//        AnchorPane.setLeftAnchor(monthlySalesLineChart, 0.0);
//
//        lineChartContainer.getChildren().add(monthlySalesLineChart);
//    }
//
//    private void setupTopProductsBarChart() {
//        // Define axis
//        CategoryAxis xAxis = new CategoryAxis();
//        NumberAxis yAxis = new NumberAxis();
//        xAxis.setLabel("Product");
//        yAxis.setLabel("Quantity Sold");
//
//        // Create chart
//        topProductsBarChart = new BarChart<>(xAxis, yAxis);
//        topProductsBarChart.setTitle("Top 5 Products");
//        topProductsBarChart.setLegendVisible(false);
//        topProductsBarChart.setAnimated(false);
//
//        AnchorPane.setTopAnchor(topProductsBarChart, 0.0);
//        AnchorPane.setRightAnchor(topProductsBarChart, 0.0);
//        AnchorPane.setBottomAnchor(topProductsBarChart, 0.0);
//        AnchorPane.setLeftAnchor(topProductsBarChart, 0.0);
//
//        barChartContainer.getChildren().add(topProductsBarChart);
//    }
//
//    private void setupButtonActions() {
//        // Filter buttons
//        applyFilterBtn.setOnAction(this::handleApplyFilter);
//        resetFilterBtn.setOnAction(this::handleResetFilter);
//
//        // Export buttons for Category Sales
//        exportCategoryChartBtn.setOnAction(event -> exportChartAsPDF(categorySalesPieChart, "Sales_by_Category"));
//        exportCategoryDataBtn.setOnAction(event -> exportDataAsCSV(categoryData, "Sales_by_Category"));
//
//        // Export buttons for Monthly Trends
//        exportTrendsChartBtn.setOnAction(event -> exportChartAsPDF(monthlySalesLineChart, "Monthly_Trends"));
//        exportTrendsDataBtn.setOnAction(event -> exportDataAsCSV(monthlyData, "Monthly_Trends"));
//
//        // Export buttons for Top Products
//        exportTopProductsChartBtn.setOnAction(event -> exportChartAsPDF(topProductsBarChart, "Top_Products"));
//        exportTopProductsDataBtn.setOnAction(event -> exportDataAsCSV(topProductsData, "Top_Products"));
//    }
//
//    private void handleApplyFilter(ActionEvent event) {
//        applyFilters();
//    }
//
//    private void handleResetFilter(ActionEvent event) {
//        // Reset filters to default values
//        startDatePicker.setValue(LocalDate.now().minusMonths(6));
//        endDatePicker.setValue(LocalDate.now());
//        categoryComboBox.getSelectionModel().selectFirst();
//
//        // Apply the reset filters
//        applyFilters();
//
//        updateStatus("Filters reset");
//    }
//
//    private void applyFilters() {
//        try {
//            LocalDate startDate = startDatePicker.getValue();
//            LocalDate endDate = endDatePicker.getValue();
//            String category = categoryComboBox.getValue();
//
//            // Validate dates
//            if (startDate == null || endDate == null) {
//                updateStatus("Please select valid dates");
//                return;
//            }
//
//            if (startDate.isAfter(endDate)) {
//                updateStatus("Start date must be before end date");
//                return;
//            }
//
//            // Get filtered sales data
//            if (category.equals("All Categories")) {
//                filteredSales = model.getFilteredSells(startDate, endDate, null);
//            } else {
//                filteredSales = model.getFilteredSells(startDate, endDate, category);
//            }
//
//            // Update all charts with new data
//            updateCategorySalesChart();
//            updateMonthlySalesChart();
//            updateTopProductsChart();
//
//            updateStatus("Data filtered successfully: " + filteredSales.size() + " sales");
//        } catch (Exception e) {
//            e.printStackTrace();
//            updateStatus("Error applying filters: " + e.getMessage());
//        }
//    }
//
//    private void updateCategorySalesChart() {
//        try {
//            // Clear existing data
//            categorySalesPieChart.getData().clear();
//
//            // Get category sales data
//            categoryData = model.getTotalSalesByCategory();
//            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
//
//            // Populate pie chart data
//            for (String[] data : categoryData) {
//                String category = data[0];
//                double totalSales = Double.parseDouble(data[1]);
//                pieChartData.add(new PieChart.Data(category + " ($" + totalSales + ")", totalSales));
//            }
//
//            categorySalesPieChart.setData(pieChartData);
//        } catch (Exception e) {
//            e.printStackTrace();
//            updateStatus("Error updating category chart: " + e.getMessage());
//        }
//    }
//
//    private void updateMonthlySalesChart() {
//        try {
//            // Clear existing data
//            monthlySalesLineChart.getData().clear();
//
//            // Get monthly sales data
//            monthlyData = model.getMonthlySalesTrends();
//
//            // Create series for the line chart
//            XYChart.Series<String, Number> series = new XYChart.Series<>();
//            series.setName("Monthly Sales");
//
//            // Populate series data
//            for (String[] data : monthlyData) {
//                String month = data[0];
//                double sales = Double.parseDouble(data[1]);
//                series.getData().add(new XYChart.Data<>(month, sales));
//            }
//
//            monthlySalesLineChart.getData().add(series);
//        } catch (Exception e) {
//            e.printStackTrace();
//            updateStatus("Error updating trends chart: " + e.getMessage());
//        }
//    }
//
//    private void updateTopProductsChart() {
//        try {
//            // Clear existing data
//            topProductsBarChart.getData().clear();
//
//            // Get top products data
//            topProductsData = model.getTopSellingProducts();
//
//            // Create series for the bar chart
//            XYChart.Series<String, Number> series = new XYChart.Series<>();
//            series.setName("Quantity Sold");
//
//            // Populate series data (limit to top 5)
//            int count = 0;
//            for (String[] data : topProductsData) {
//                if (count >= 5) break;
//
//                String product = data[0];
//                int quantity = Integer.parseInt(data[1]);
//                series.getData().add(new XYChart.Data<>(product, quantity));
//                count++;
//            }
//
//            topProductsBarChart.getData().add(series);
//        } catch (Exception e) {
//            e.printStackTrace();
//            updateStatus("Error updating products chart: " + e.getMessage());
//        }
//    }
//
//    private void exportChartAsPDF(Chart chart, String filename) {
//        try {
//            // Create a file chooser
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Save Chart as PDF");
//            fileChooser.getExtensionFilters().add(
//                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
//            fileChooser.setInitialFileName(filename + ".pdf");
//
//            // Show save dialog
//            File file = fileChooser.showSaveDialog(chart.getScene().getWindow());
//
//            if (file != null) {
//                // Create a temporary image of the chart
//                File tempImageFile = File.createTempFile("chart", ".png");
//
//                // Create JFreeChart equivalent based on chart type
//                JFreeChart jFreeChart = null;
//
//                if (chart instanceof PieChart) {
//                    DefaultPieDataset dataset = new DefaultPieDataset();
//                    for (PieChart.Data data : ((PieChart) chart).getData()) {
//                        dataset.setValue(data.getName(), data.getPieValue());
//                    }
//                    jFreeChart = ChartFactory.createPieChart(
//                            chart.getTitle(),
//                            dataset,
//                            true,
//                            true,
//                            false
//                    );
//                } else if (chart instanceof LineChart) {
//                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//                    for (XYChart.Series<String, Number> series : ((LineChart<String, Number>) chart).getData()) {
//                        for (XYChart.Data<String, Number> data : series.getData()) {
//                            dataset.addValue(data.getYValue(), series.getName(), data.getXValue());
//                        }
//                    }
//                    jFreeChart = ChartFactory.createLineChart(
//                            chart.getTitle(),
//                            ((LineChart<String, Number>) chart).getXAxis().getLabel(),
//                            ((LineChart<String, Number>) chart).getYAxis().getLabel(),
//                            dataset,
//                            PlotOrientation.VERTICAL,
//                            true,
//                            true,
//                            false
//                    );
//                } else if (chart instanceof BarChart) {
//                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//                    for (XYChart.Series<String, Number> series : ((BarChart<String, Number>) chart).getData()) {
//                        for (XYChart.Data<String, Number> data : series.getData()) {
//                            dataset.addValue(data.getYValue(), series.getName(), data.getXValue());
//                        }
//                    }
//                    jFreeChart = ChartFactory.createBarChart(
//                            chart.getTitle(),
//                            ((BarChart<String, Number>) chart).getXAxis().getLabel(),
//                            ((BarChart<String, Number>) chart).getYAxis().getLabel(),
//                            dataset,
//                            PlotOrientation.VERTICAL,
//                            true,
//                            true,
//                            false
//                    );
//                }
//
//                if (jFreeChart != null) {
//                    // Save chart as image
//                    ChartUtils.saveChartAsPNG(tempImageFile, jFreeChart, 800, 600);
//
//                    // Create PDF with the chart image
//                    Document document = new Document();
//                    PdfWriter.getInstance(document, new FileOutputStream(file));
//                    document.open();
//
//                    // Add title and date
//                    document.add(new Paragraph("Report: " + chart.getTitle()));
//                    document.add(new Paragraph("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))));
//                    document.add(new Paragraph("Period: " +
//                            startDatePicker.getValue().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) +
//                            " - " +
//                            endDatePicker.getValue().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))));
//                    document.add(new Paragraph("\n"));
//
//                    // Add chart image
//                    com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(tempImageFile.getAbsolutePath());
//                    image.scaleToFit(500, 400);
//                    document.add(image);
//
//                    // Close document
//                    document.close();
//
//                    // Delete temporary file
//                    tempImageFile.delete();
//
//                    updateStatus("Chart exported successfully to PDF: " + file.getAbsolutePath());
//                }
//            }
//        } catch (IOException | DocumentException e) {
//            e.printStackTrace();
//            updateStatus("Error exporting chart to PDF: " + e.getMessage());
//        }
//    }
//
//    private void exportDataAsCSV(ObservableList<String[]> data, String filename) {
//        try {
//            // Create a file chooser
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Save Data as CSV");
//            fileChooser.getExtensionFilters().add(
//                    new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
//            fileChooser.setInitialFileName(filename + ".csv");
//
//            // Show save dialog
//            File file = fileChooser.showSaveDialog(chartsTabPane.getScene().getWindow());
//
//            if (file != null) {
//                // Create CSV file
//                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
//                    // Write header based on the selected tab
//                    if (filename.equals("Sales_by_Category")) {
//                        writer.println("Category,Total Amount");
//                    } else if (filename.equals("Monthly_Trends")) {
//                        writer.println("Month,Total Amount");
//                    } else if (filename.equals("Top_Products")) {
//                        writer.println("Product,Quantity");
//                    }
//
//                    // Write data
//                    for (String[] row : data) {
//                        writer.println(row[0] + "," + row[1]);
//                    }
//                }
//
//                updateStatus("Data exported successfully to CSV: " + file.getAbsolutePath());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            updateStatus("Error exporting data to CSV: " + e.getMessage());
//        }
//    }
//
//    private void updateStatus(String message) {
//        statusLabel.setText(message);
//    }
//}