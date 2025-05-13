package com.example.venteanalystfx.Controllers.User;

import com.example.venteanalystfx.Models.Model;
import com.example.venteanalystfx.Models.Sale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class VisualizeReusltsController implements Initializable {

    // Chart containers
    @FXML private TabPane chartTabPane;
    @FXML private StackPane barChartContainer;
    @FXML private StackPane pieChartContainer;
    @FXML private StackPane lineChartContainer;
    @FXML private StackPane scatterChartContainer;
    @FXML private StackPane areaChartContainer;

    // Filter controls
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> chartTypeComboBox;
    @FXML private ComboBox<String> aggregationComboBox;
    @FXML private Button refreshButton;
    @FXML private Label statusLabel;

    // Chart instances
    private BarChart<String, Number> barChart;
    private PieChart pieChart;
    private LineChart<String, Number> lineChart;
    private ScatterChart<Number, Number> scatterChart;
    private AreaChart<String, Number> areaChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup date pickers
        startDatePicker.setValue(LocalDate.now().minusMonths(1));
        endDatePicker.setValue(LocalDate.now());

        // Setup comboboxes
        categoryComboBox.getItems().addAll("All", "Informatique", "Electronique");
        categoryComboBox.setValue("All");

        chartTypeComboBox.getItems().addAll("Daily Sales", "Product Distribution", "Category Comparison",
                "Price vs Quantity", "Sales Trend");
        chartTypeComboBox.setValue("Daily Sales");

        aggregationComboBox.getItems().addAll("Total Sales", "Quantity Sold", "Average Price");
        aggregationComboBox.setValue("Total Sales");

        // Initialize charts
        initializeCharts();

        // Setup refresh button
        refreshButton.setOnAction(event -> refreshCharts());

        // Load data initially
        refreshCharts();
    }

    private void initializeCharts() {
        // Bar Chart
        CategoryAxis xAxisBar = new CategoryAxis();
        NumberAxis yAxisBar = new NumberAxis();
        xAxisBar.setLabel("Date");
        yAxisBar.setLabel("Value");
        barChart = new BarChart<>(xAxisBar, yAxisBar);
        barChart.setTitle("Sales by Date");
        barChart.setAnimated(false);
        barChartContainer.getChildren().add(barChart);

        // Pie Chart
        pieChart = new PieChart();
        pieChart.setTitle("Sales Distribution");
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(true);
        pieChartContainer.getChildren().add(pieChart);

        // Line Chart
        CategoryAxis xAxisLine = new CategoryAxis();
        NumberAxis yAxisLine = new NumberAxis();
        xAxisLine.setLabel("Date");
        yAxisLine.setLabel("Value");
        lineChart = new LineChart<>(xAxisLine, yAxisLine);
        lineChart.setTitle("Sales Trend");
        lineChart.setAnimated(false);
        lineChartContainer.getChildren().add(lineChart);

        // Scatter Chart
        NumberAxis xAxisScatter = new NumberAxis();
        NumberAxis yAxisScatter = new NumberAxis();
        xAxisScatter.setLabel("Price");
        yAxisScatter.setLabel("Quantity");
        scatterChart = new ScatterChart<>(xAxisScatter, yAxisScatter);
        scatterChart.setTitle("Price vs Quantity");
        scatterChartContainer.getChildren().add(scatterChart);

        // Area Chart
        CategoryAxis xAxisArea = new CategoryAxis();
        NumberAxis yAxisArea = new NumberAxis();
        xAxisArea.setLabel("Date");
        yAxisArea.setLabel("Value");
        areaChart = new AreaChart<>(xAxisArea, yAxisArea);
        areaChart.setTitle("Cumulative Sales");
        areaChartContainer.getChildren().add(areaChart);
    }

    @FXML
    private void refreshCharts() {
        try {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String category = categoryComboBox.getValue();
            String chartType = chartTypeComboBox.getValue();
            String aggregation = aggregationComboBox.getValue();

            if (startDate == null || endDate == null) {
                statusLabel.setText("Please select valid dates");
                return;
            }

            if (startDate.isAfter(endDate)) {
                statusLabel.setText("Start date must be before end date");
                return;
            }

            // Get data from the model
            ObservableList<Sale> salesData = Model.getInstance().getFilteredSells(startDate, endDate, category);

            if (salesData.isEmpty()) {
                statusLabel.setText("No data available for the selected filters");
                clearAllCharts();
                return;
            }

            // Update the charts based on the selected chart type
            switch (chartType) {
                case "Daily Sales":
                    updateDailySalesCharts(salesData, aggregation);
                    chartTabPane.getSelectionModel().select(0); // Select bar chart tab
                    break;
                case "Product Distribution":
                    updateProductDistributionCharts(salesData, aggregation);
                    chartTabPane.getSelectionModel().select(1); // Select pie chart tab
                    break;
                case "Category Comparison":
                    updateCategoryComparisonCharts(salesData, aggregation);
                    chartTabPane.getSelectionModel().select(1); // Select pie chart tab
                    break;
                case "Price vs Quantity":
                    updatePriceQuantityCharts(salesData);
                    chartTabPane.getSelectionModel().select(3); // Select scatter chart tab
                    break;
                case "Sales Trend":
                    updateSalesTrendCharts(salesData, aggregation);
                    chartTabPane.getSelectionModel().select(2); // Select line chart tab
                    break;
            }

            statusLabel.setText("Charts updated successfully with " + salesData.size() + " records");
        } catch (Exception e) {
            statusLabel.setText("Error updating charts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearAllCharts() {
        barChart.getData().clear();
        pieChart.getData().clear();
        lineChart.getData().clear();
        scatterChart.getData().clear();
        areaChart.getData().clear();
    }

    private void updateDailySalesCharts(ObservableList<Sale> salesData, String aggregation) {
        // Clear previous data
        barChart.getData().clear();
        lineChart.getData().clear();
        areaChart.getData().clear();

        // Group data by date
        Map<LocalDate, List<Sale>> salesByDate = salesData.stream()
                .collect(Collectors.groupingBy(sale -> sale.dateVenteProperty().get()));

        // Sort dates
        List<LocalDate> sortedDates = new ArrayList<>(salesByDate.keySet());
        Collections.sort(sortedDates);

        // Create series for charts
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        barSeries.setName(aggregation);

        XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();
        lineSeries.setName(aggregation);

        XYChart.Series<String, Number> areaSeries = new XYChart.Series<>();
        areaSeries.setName(aggregation);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        double cumulativeTotal = 0;

        for (LocalDate date : sortedDates) {
            List<Sale> dailySales = salesByDate.get(date);
            double value = calculateAggregatedValue(dailySales, aggregation);

            String dateStr = date.format(formatter);
            barSeries.getData().add(new XYChart.Data<>(dateStr, value));
            lineSeries.getData().add(new XYChart.Data<>(dateStr, value));

            cumulativeTotal += value;
            areaSeries.getData().add(new XYChart.Data<>(dateStr, cumulativeTotal));
        }

        barChart.getData().add(barSeries);
        lineChart.getData().add(lineSeries);
        areaChart.getData().add(areaSeries);

        // Set titles
        barChart.setTitle(aggregation + " by Date");
        lineChart.setTitle(aggregation + " Trend");
        areaChart.setTitle("Cumulative " + aggregation);
    }

    private void updateProductDistributionCharts(ObservableList<Sale> salesData, String aggregation) {
        // Clear previous data
        pieChart.getData().clear();

        // Group data by product
        Map<String, List<Sale>> salesByProduct = salesData.stream()
                .collect(Collectors.groupingBy(sale -> sale.produitProperty().get()));

        // Create pie chart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Map.Entry<String, List<Sale>> entry : salesByProduct.entrySet()) {
            double value = calculateAggregatedValue(entry.getValue(), aggregation);
            pieChartData.add(new PieChart.Data(entry.getKey() + " (" + String.format("%.2f", value) + ")", value));
        }

        pieChart.setData(pieChartData);
        pieChart.setTitle(aggregation + " by Product");
    }

    private void updateCategoryComparisonCharts(ObservableList<Sale> salesData, String aggregation) {
        // Clear previous data
        pieChart.getData().clear();
        barChart.getData().clear();

        // Group data by category
        Map<String, List<Sale>> salesByCategory = salesData.stream()
                .collect(Collectors.groupingBy(sale -> sale.categorieProperty().get()));

        // Create pie chart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        barSeries.setName(aggregation);

        for (Map.Entry<String, List<Sale>> entry : salesByCategory.entrySet()) {
            double value = calculateAggregatedValue(entry.getValue(), aggregation);
            pieChartData.add(new PieChart.Data(entry.getKey() + " (" + String.format("%.2f", value) + ")", value));
            barSeries.getData().add(new XYChart.Data<>(entry.getKey(), value));
        }

        pieChart.setData(pieChartData);
        pieChart.setTitle(aggregation + " by Category");

        barChart.getData().add(barSeries);
        barChart.setTitle(aggregation + " by Category");
    }

    private void updatePriceQuantityCharts(ObservableList<Sale> salesData) {
        // Clear previous data
        scatterChart.getData().clear();

        // Create scatter chart data
        XYChart.Series<Number, Number> scatterSeries = new XYChart.Series<>();
        scatterSeries.setName("Price vs Quantity");

        for (Sale sale : salesData) {
            scatterSeries.getData().add(new XYChart.Data<>(
                    sale.prixUnitaireProperty().get(),
                    sale.quantiteProperty().get()
            ));
        }

        scatterChart.getData().add(scatterSeries);
    }

    private void updateSalesTrendCharts(ObservableList<Sale> salesData, String aggregation) {
        // Clear previous data
        lineChart.getData().clear();
        areaChart.getData().clear();

        // Group data by date
        Map<LocalDate, List<Sale>> salesByDate = salesData.stream()
                .collect(Collectors.groupingBy(sale -> sale.dateVenteProperty().get()));

        // Group data by category
        Map<String, List<Sale>> salesByCategory = salesData.stream()
                .collect(Collectors.groupingBy(sale -> sale.categorieProperty().get()));

        // Sort dates
        List<LocalDate> sortedDates = new ArrayList<>(salesByDate.keySet());
        Collections.sort(sortedDates);

        // Create line chart data by category
        Map<String, XYChart.Series<String, Number>> categorySeriesMap = new HashMap<>();

        for (String category : salesByCategory.keySet()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(category);
            categorySeriesMap.put(category, series);
        }

        // Create area chart for cumulative total
        XYChart.Series<String, Number> cumulativeSeries = new XYChart.Series<>();
        cumulativeSeries.setName("Cumulative " + aggregation);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        double cumulativeTotal = 0;

        // Populate series with data
        for (LocalDate date : sortedDates) {
            String dateStr = date.format(formatter);

            // Get sales for this date
            List<Sale> dailySales = salesByDate.get(date);

            // Group by category
            Map<String, List<Sale>> dailySalesByCategory = dailySales.stream()
                    .collect(Collectors.groupingBy(sale -> sale.categorieProperty().get()));

            // Add data points for each category
            for (String category : salesByCategory.keySet()) {
                List<Sale> categorySales = dailySalesByCategory.getOrDefault(category, Collections.emptyList());
                if (!categorySales.isEmpty()) {
                    double value = calculateAggregatedValue(categorySales, aggregation);
                    categorySeriesMap.get(category).getData().add(new XYChart.Data<>(dateStr, value));
                }
            }

            // Calculate cumulative total
            double dateTotal = calculateAggregatedValue(dailySales, aggregation);
            cumulativeTotal += dateTotal;
            cumulativeSeries.getData().add(new XYChart.Data<>(dateStr, cumulativeTotal));
        }

        // Add all series to the charts
        for (XYChart.Series<String, Number> series : categorySeriesMap.values()) {
            lineChart.getData().add(series);
        }

        areaChart.getData().add(cumulativeSeries);

        // Set titles
        lineChart.setTitle(aggregation + " Trend by Category");
        areaChart.setTitle("Cumulative " + aggregation);
    }

    private double calculateAggregatedValue(List<Sale> sales, String aggregationType) {
        switch (aggregationType) {
            case "Total Sales":
                return sales.stream().mapToDouble(sale -> sale.totalProperty().get()).sum();
            case "Quantity Sold":
                return sales.stream().mapToInt(sale -> sale.quantiteProperty().get()).sum();
            case "Average Price":
                if (sales.isEmpty()) return 0;
                return sales.stream().mapToDouble(sale -> sale.prixUnitaireProperty().get()).average().orElse(0);
            default:
                return sales.stream().mapToDouble(sale -> sale.totalProperty().get()).sum();
        }
    }
}