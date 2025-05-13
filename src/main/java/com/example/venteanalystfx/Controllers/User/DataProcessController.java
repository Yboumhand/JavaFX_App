package com.example.venteanalystfx.Controllers.User;

import com.example.venteanalystfx.Models.Model;
import com.example.venteanalystfx.Models.Sale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Comparator;

public class DataProcessController implements Initializable {

    // Tables pour les ventes par catégorie
    public TableView<CategorySale> categorySalesTable;
    public TableColumn<CategorySale, String> categoryColumn;
    public TableColumn<CategorySale, String> totalSalesColumn;
    private final Model model = Model.getInstance();

    // Tables pour les produits les plus vendus
    public TableView<ProductSale> topProductsTable;
    public TableColumn<ProductSale, String> productNameColumn;
    public TableColumn<ProductSale, Integer> quantitySoldColumn;

    // Tables pour les prix des produits
    public TableView<PricedProduct> productPricesTable;
    public TableColumn<PricedProduct, String> priceProductColumn;
    public TableColumn<PricedProduct, String> priceValueColumn;

    // Labels pour les statistiques
    public VBox statsContainer;
    public Label totalSalesLabel;
    public Label avgSalesLabel;
    public Label mostExpensiveLabel;
    public Label leastExpensiveLabel;
    public Label bestSellingCategoryLabel;
    public Label bestSellingMonthLabel;

    // Graphiques
    public BarChart<String, Number> monthlySalesChart;
    public CategoryAxis monthAxis;
    public NumberAxis salesAxis;
    public PieChart categorySalesPieChart;
    public PieChart productQuantityPieChart;

    // Table des données brutes
    public TableView<Sale> allSalesTable;
    public TableColumn<Sale, Integer> idColumn;
    public TableColumn<Sale, LocalDate> dateColumn;
    public TableColumn<Sale, String> rawProductColumn;
    public TableColumn<Sale, String> rawCategoryColumn;
    public TableColumn<Sale, Integer> quantityColumn;
    public TableColumn<Sale, Float> unitPriceColumn;
    public TableColumn<Sale, Float> totalColumn;

    // Classes pour les modèles de données personnalisés
    public static class CategorySale {
        private final String category;
        private final String totalSales;

        public CategorySale(String category, String totalSales) {
            this.category = category;
            this.totalSales = totalSales;
        }

        public String getCategory() {
            return category;
        }

        public String getTotalSales() {
            return totalSales;
        }
    }

    public static class ProductSale {
        private final String productName;
        private final int quantitySold;

        public ProductSale(String productName, int quantitySold) {
            this.productName = productName;
            this.quantitySold = quantitySold;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantitySold() {
            return quantitySold;
        }
    }

    public static class PricedProduct {
        private final String productName;
        private final String price;

        public PricedProduct(String productName, String price) {
            this.productName = productName;
            this.price = price;
        }

        public String getProductName() {
            return productName;
        }

        public String getPrice() {
            return price;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser tous les composants
        initializeRawDataTable();
        initializeCategorySalesTable();
        initializeTopProductsTable();
        initializeProductPricesTable();
        loadStatistics();
        createMonthlySalesChart();
        createCategorySalesPieChart();
        createProductQuantityPieChart();
    }

    private void initializeRawDataTable() {
        // Configuration des colonnes pour la table de données brutes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idVente"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateVente"));
        rawProductColumn.setCellValueFactory(new PropertyValueFactory<>("produit"));
        rawCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Chargement des données
        allSalesTable.setItems(Model.getInstance().getAllSells());
    }

    private void initializeCategorySalesTable() {
        // Configuration des colonnes pour la table des ventes par catégorie
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        totalSalesColumn.setCellValueFactory(new PropertyValueFactory<>("totalSales"));

        // Chargement des données à partir de l'ObservableList<PieChart.Data>
        ObservableList<CategorySale> categoryData = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> pieChartData = model.getCategorySalesData(); // Get the PieChart data

        for (PieChart.Data data : pieChartData) {
            categoryData.add(new CategorySale(data.getName(), String.format("%.2f DH", data.getPieValue())));
        }

        categorySalesTable.setItems(categoryData);
    }

    private void initializeTopProductsTable() {
        // Configuration des colonnes pour la table des produits les plus vendus
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantitySoldColumn.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));

        // Chargement des données
        ObservableList<ProductSale> productData = FXCollections.observableArrayList();
        ObservableList<String[]> topProducts = Model.getInstance().getTopSellingProducts();

        for (String[] data : topProducts) {
            productData.add(new ProductSale(data[0], Integer.parseInt(data[1])));
        }

        // Trier par quantité (décroissant)
        productData.sort(Comparator.comparing(ProductSale::getQuantitySold).reversed());

        topProductsTable.setItems(productData);
    }

    private void initializeProductPricesTable() {
        // Configuration des colonnes pour la table des prix des produits
        priceProductColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        priceValueColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Chargement des données
        ObservableList<PricedProduct> priceData = FXCollections.observableArrayList();

        // Récupérer tous les produits uniques avec leur prix
        ObservableList<Sale> allSales = Model.getInstance().getAllSells();
        List<String> processedProducts = new ArrayList<>();

        for (Sale sale : allSales) {
            String productName = sale.produitProperty().get();
            if (!processedProducts.contains(productName)) {
                processedProducts.add(productName);
                priceData.add(new PricedProduct(productName, String.format("%.2f DH", sale.prixUnitaireProperty().get())));
            }
        }
        priceData.sort(Comparator.comparing(p -> {
            String priceStr = p.getPrice().replace(" DH", "").replace(",", "."); // Remplacer la virgule par un point
            return -Float.parseFloat(priceStr);
        }));

        productPricesTable.setItems(priceData);
    }

    private void loadStatistics() {
        // Calcul du total des ventes
        float totalSales = 0;
        ObservableList<Sale> allSales = Model.getInstance().getAllSells();

        for (Sale sale : allSales) {
            totalSales += sale.totalProperty().get();
        }

        // Calcul de la moyenne des ventes
        float avgSale = allSales.isEmpty() ? 0 : totalSales / allSales.size();

        // Produit le plus cher et le moins cher
        String[] mostExpensive = Model.getInstance().getMostExpensiveProduct();
        String[] leastExpensive = Model.getInstance().getLeastExpensiveProduct();

        // Meilleure catégorie de vente
        ObservableList<String[]> categorySales = Model.getInstance().getTotalSalesByCategory();
        String bestCategory = "";
        float maxCategorySales = 0;

        if (categorySales != null) {
            for (String[] data : categorySales) {
                if (data != null && data.length > 1 && data[1] != null && !data[1].isEmpty()) {
                    try {
                        float categorySale = Float.parseFloat(data[1].trim().replace(" DH", "").replace(",", "."));
                        if (bestCategory.isEmpty() || categorySale > maxCategorySales) { // Simplification ici
                            maxCategorySales = categorySale;
                            bestCategory = data[0];
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Erreur de format numérique pour la catégorie : " + data[0] + " - " + data[1]);
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Données de catégorie invalides : " + java.util.Arrays.toString(data));
                }
            }
        }

        // Mois avec les meilleures ventes
        ObservableList<String[]> monthlySales = Model.getInstance().getMonthlySalesTrends();
        String bestMonth = "";
        float maxMonthlySales = 0;

        if (monthlySales != null) {
            for (String[] data : monthlySales) {
                if (data != null && data.length > 1 && data[1] != null && !data[1].isEmpty()) {
                    try {
                        float monthlySale = Float.parseFloat(data[1].trim().replace(" DH", "").replace(",", "."));
                        if (monthlySale > maxMonthlySales) {
                            maxMonthlySales = monthlySale;
                            bestMonth = data[0];
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Erreur de format numérique (meilleur mois) : " + data[1]);
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Données de ventes mensuelles invalides : " + java.util.Arrays.toString(data));
                }
            }
        }


// Mise à jour des labels de statistiques
        totalSalesLabel.setText(String.format("Total des ventes: %.2f DH", totalSales));
        avgSalesLabel.setText(String.format("Vente moyenne: %.2f DH", avgSale));

        if (mostExpensive != null && mostExpensive.length > 1 && mostExpensive[1] != null && !mostExpensive[1].isEmpty()) {
            try {
                mostExpensiveLabel.setText(String.format("Produit le plus cher: %s (%.2f DH)",
                        mostExpensive[0], Float.parseFloat(mostExpensive[1].trim().replace(" DH", "").replace(",", "."))));
            } catch (NumberFormatException e) {
                System.err.println("Erreur de format numérique (plus cher) : " + mostExpensive[1]);
                e.printStackTrace();
                mostExpensiveLabel.setText("Produit le plus cher: Inconnu"); // or some default value
            }
        } else {
            mostExpensiveLabel.setText("Produit le plus cher: Inconnu");
        }


        if (leastExpensive != null && leastExpensive.length > 1 && leastExpensive[1] != null && !leastExpensive[1].isEmpty()) {
            try {
                leastExpensiveLabel.setText(String.format("Produit le moins cher: %s (%.2f DH)",
                        leastExpensive[0], Float.parseFloat(leastExpensive[1].trim().replace(" DH", "").replace(",", "."))));
            } catch (NumberFormatException e) {
                System.err.println("Erreur de format numérique (moins cher) : " + leastExpensive[1]);
                e.printStackTrace();
                leastExpensiveLabel.setText("Produit le moins cher: Inconnu"); // or some default value
            }
        } else {
            leastExpensiveLabel.setText("Produit le moins cher: Inconnu");
        }


        bestSellingCategoryLabel.setText("Meilleure catégorie: " + bestCategory);

// Formater le mois pour l'affichage (YYYY-MM à Mois Année)
        if (!bestMonth.isEmpty()) {
            String[] parts = bestMonth.split("-");
            if (parts.length == 2) {
                String year = parts[0];
                String month = parts[1];
                String[] monthNames = {"", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
                try {
                    int monthIndex = Integer.parseInt(month);
                    if (monthIndex >= 1 && monthIndex <= 12) {
                        String formattedMonth = monthNames[monthIndex] + " " + year;
                        bestSellingMonthLabel.setText("Mois avec meilleures ventes: " + formattedMonth);
                    } else {
                        bestSellingMonthLabel.setText("Mois avec meilleures ventes: Inconnu");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Erreur de format de mois : " + month);
                    e.printStackTrace();
                    bestSellingMonthLabel.setText("Mois avec meilleures ventes: Inconnu");
                }
            } else {
                bestSellingMonthLabel.setText("Mois avec meilleures ventes: Inconnu");
            }
        } else {
            bestSellingMonthLabel.setText("Mois avec meilleures ventes: Inconnu");
        }
    }

    private void createMonthlySalesChart() {
        // Créer une série pour le graphique des ventes mensuelles
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ventes Mensuelles");

        // Récupérer les données
        ObservableList<String[]> monthlySales = Model.getInstance().getMonthlySalesTrends();

        if (monthlySales != null) {
            // Formater les données pour le graphique
            for (String[] data : monthlySales) {
                if (data != null && data.length > 1 && data[1] != null && !data[1].isEmpty()) {
                    try {
                        String month = formatMonth(data[0]);
                        float sale = Float.parseFloat(data[1].trim().replace(" DH", "").replace(",", "."));
                        series.getData().add(new XYChart.Data<>(month, sale));
                    } catch (NumberFormatException e) {
                        System.err.println("Erreur de format numérique (graphique mensuel) : " + data[1]);
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Données de ventes mensuelles invalides (graphique) : " + java.util.Arrays.toString(data));
                }
            }

            // Ajouter la série au graphique
            monthlySalesChart.getData().add(series);
        }
    }

    private String formatMonth(String yearMonth) {
        // Formater "YYYY-MM" en "MMM Année"
        if (yearMonth != null && !yearMonth.isEmpty() && yearMonth.contains("-")) {
            String[] parts = yearMonth.split("-");
            if (parts.length == 2) {
                String year = parts[0];
                String month = parts[1];
                String[] monthAbbr = {"", "Jan", "Fév", "Mar", "Avr", "Mai", "Juin",
                        "Juil", "Aoû", "Sep", "Oct", "Nov", "Déc"};
                try {
                    int monthIndex = Integer.parseInt(month);
                    if (monthIndex >= 1 && monthIndex <= 12) {
                        return monthAbbr[monthIndex] + " " + year;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Erreur de format de mois (formatMonth) : " + month);
                    e.printStackTrace();
                }
            }
        }
        return "Inconnu"; // Default value in case of error
    }

    private void createCategorySalesPieChart() {
        // Créer un graphique circulaire pour les ventes par catégorie
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        ObservableList<String[]> categorySales = Model.getInstance().getTotalSalesByCategory();

        if (categorySales != null) {
            for (String[] data : categorySales) {
                if (data != null && data.length > 1 && data[1] != null && !data[1].isEmpty()) {
                    try {
                        pieChartData.add(new PieChart.Data(data[0], Float.parseFloat(data[1].trim().replace(" DH", "").replace(",", "."))));
                    } catch (NumberFormatException e) {
                        System.err.println("Erreur de format numérique (camembert catégorie) : " + data[1]);
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Données de ventes par catégorie invalides (camembert) : " + java.util.Arrays.toString(data));
                }
            }
            categorySalesPieChart.setData(pieChartData);
        }
    }

    private void createProductQuantityPieChart() {
        // Créer un graphique circulaire pour les 5 produits les plus vendus
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        ObservableList<String[]> topProducts = Model.getInstance().getTopSellingProducts();

        if (topProducts != null) {
            // Trier par quantité
            List<String[]> sortedProducts = new ArrayList<>(topProducts);
            Collections.sort(sortedProducts, (a, b) -> Integer.compare(Integer.parseInt(b[1]), Integer.parseInt(a[1])));

            // Prendre les 5 premiers produits
            int count = 0;
            for (String[] data : sortedProducts) {
                if (data != null && data.length > 1 && data[1] != null && !data[1].isEmpty()) {
                    try {
                        pieChartData.add(new PieChart.Data(data[0], Integer.parseInt(data[1])));
                        count++;
                        if (count >= 5) break;
                    } catch (NumberFormatException e) {
                        System.err.println("Erreur de format numérique (camembert produit) : " + data[1]);
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Données de produits les plus vendus invalides (camembert) : " + java.util.Arrays.toString(data));
                }
            }
            productQuantityPieChart.setData(pieChartData);
        }
    }
}