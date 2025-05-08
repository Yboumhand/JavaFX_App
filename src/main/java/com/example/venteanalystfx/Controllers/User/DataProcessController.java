package com.example.venteanalystfx.Controllers.User;

import com.example.venteanalystfx.Models.Model;
import com.example.venteanalystfx.Models.Sale;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DataProcessController {

    @FXML private ComboBox<String> categoryComboBox;
    @FXML private Label totalSalesLabel;
    @FXML private Label mostSoldLabel;
    @FXML private Label mostExpensiveLabel;
    @FXML private Label cheapestLabel;
    @FXML private BarChart<String, Number> monthlySalesChart;

    private final Model model = Model.getInstance();

    @FXML
    public void initialize() {
        // Charger les catégories
        Set<String> categories = model.getAllSells()
                .stream().map(sale -> sale.categorieProperty().get())
                .collect(Collectors.toSet());
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));

        // Initialiser les analyses
        showMonthlyTrends();
        showOverallStats();
    }

    public void onSearchCategory() {
        String category = categoryComboBox.getValue();
        if (category == null) return;

        List<Sale> filtered = model.getAllSells().stream()
                .filter(s -> s.categorieProperty().get().equals(category))
                .collect(Collectors.toList());

        float total = (float) filtered.stream().mapToDouble(sale -> sale.totalProperty().get()).sum();
        totalSalesLabel.setText("Total des ventes : " + total + " €");
    }

    private void showMonthlyTrends() {
        Map<String, Float> monthlySums = new HashMap<>();

        for (Sale sale : model.getAllSells()) {
            String month = sale.dateVenteProperty().get().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            monthlySums.put(month, monthlySums.getOrDefault(month, 0f) + sale.totalProperty().get());
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ventes");

        monthlySums.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        monthlySalesChart.getData().clear();
        monthlySalesChart.getData().add(series);
    }

    private void showOverallStats() {
        List<Sale> sells = model.getAllSells();

        Map<String, Integer> productQuantities = new HashMap<>();
        Sale mostExpensive = null;
        Sale cheapest = null;

        for (Sale sale : sells) {
            productQuantities.put(sale.produitProperty().get(),
                    productQuantities.getOrDefault(sale.produitProperty().get(), 0) + sale.quantiteProperty().get());

            if (mostExpensive == null || sale.prixUnitaireProperty().get() > (mostExpensive != null ? mostExpensive.prixUnitaireProperty().get() : Float.MIN_VALUE))
                mostExpensive = sale;

            if (cheapest == null || sale.prixUnitaireProperty().get() < (cheapest != null ? cheapest.prixUnitaireProperty().get() : Float.MAX_VALUE))
                cheapest = sale;
        }

        // Produit le plus vendu
        String mostSoldProduct = productQuantities.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("Aucun");

        mostSoldLabel.setText("Produit le plus vendu : " + mostSoldProduct);
        mostExpensiveLabel.setText("Produit le plus cher : " + (mostExpensive != null ? mostExpensive.produitProperty().get() + " (" + String.format("%.2f", mostExpensive.prixUnitaireProperty().get()) + " €)" : "N/A"));
        cheapestLabel.setText("Produit le moins cher : " + (cheapest != null ? cheapest.produitProperty().get() + " (" + String.format("%.2f", cheapest.prixUnitaireProperty().get()) + " €)" : "N/A"));
    }
}