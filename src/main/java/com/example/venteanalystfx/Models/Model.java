package com.example.venteanalystfx.Models;

import com.example.venteanalystfx.Views.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private final DatabaseDriver databaseDriver;

    // User Data Section
    private final User user;
    private boolean userLoginSuccessFlag; // evaluation of the login credentials (success or failure)
    // Salles Data Section
    private final ObservableList<Sale> sells;


    private Model() {
        this.viewFactory = new ViewFactory();
        this.databaseDriver = new DatabaseDriver();
        // User Data Section
        this.userLoginSuccessFlag = false; // initially set to false (always)
        // initially fields are empty for the user
        this.user = new User("", "", "");

        // Sells Data Section
        this.sells = FXCollections.observableArrayList();
        loadAllSells(); // Charger les ventes au démarrage du Model
    }

    // to return a single instance of the model class
    public static synchronized Model getInstance() {
        if (model == null) { // if hasn't been created yet
            model = new Model();
        }
        return model; // so we returned it
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public DatabaseDriver getDatabaseDriver() {return databaseDriver;}

    // User Method Section (getter and setter)

    public boolean getUserClientLoginSuccessFlag() {
        return this.userLoginSuccessFlag;
    }
    public void setUserLoginSuccessFlag(boolean flag) {
        this.userLoginSuccessFlag = flag;
    }

    public User getUser() {
        return user;
    }

    public void evaluateUserCredentials(String username, String password) {
        ResultSet resultSet = databaseDriver.getUserData(username, password);
        try {
            if (resultSet.isBeforeFirst()) {
                this.user.firstNameProperty().set(resultSet.getString("prenom"));
                this.user.lastNameProperty().set(resultSet.getString("nom"));
                this.user.userNameProperty().set(resultSet.getString("nom_utilisateur"));
                this.userLoginSuccessFlag = true;
            }
        } catch(Exception e) {
            e.printStackTrace();

        }
    }

    // Sells Method Section

    public ObservableList<Sale> getAllSells() {
        sells.clear(); // Effacer la liste existante
        loadAllSells(); // Recharger les données depuis la base de données
        return sells;
    }

    public ObservableList<PieChart.Data> getCategorySalesData() {
        ObservableList<PieChart.Data> categorySalesData = FXCollections.observableArrayList();

        // Get category sales data from the database
        ResultSet resultSet = this.databaseDriver.getTotalSalesByCategory();

        try {
            while (resultSet.next()) {
                String category = resultSet.getString("categorie");
                float totalSales = resultSet.getFloat("total_par_categorie");

                // Add data to the observable list
                categorySalesData.add(new PieChart.Data(category, totalSales));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categorySalesData;
    }



    private void loadAllSells() {
        ResultSet resultSet = databaseDriver.getAllSalesData();
        try {
            while (resultSet.next()) {
                int idVente = resultSet.getInt("id_vente");
                LocalDate dateVente = LocalDate.parse(resultSet.getString("date_vente"));
                String produit = resultSet.getString("produit");
                String categorie = resultSet.getString("categorie");
                int quantite = resultSet.getInt("quantite");
                float prixUnitaire = resultSet.getFloat("prix_unitaire");
                float total = resultSet.getFloat("total");

                Sale sell = new Sale(idVente, dateVente, produit, categorie, quantite, prixUnitaire, total);
                sells.add(sell);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addNewSale(String product, String category, int quantity, float unitPrice, LocalDate saleDate) {
        boolean success = databaseDriver.insertSale(product, category, quantity, unitPrice, saleDate);
        if (success) {
            // Recalculate the total
            float total = quantity * unitPrice;

            // Créer un nouvel objet Sell avec un ID temporaire (sera mis à jour lors du prochain chargement)
            Sale newSell = new Sale(0, saleDate, product, category, quantity, unitPrice, total);
            sells.add(newSell);

            // Recharger toutes les ventes pour obtenir l'ID correct
            loadAllSells();
        }
        return success;
    }

    // Used for the purpose of getting the filtered salles (records matching the filters choosed by the user)
    public ObservableList<Sale> getFilteredSells(LocalDate startDate, LocalDate endDate, String category) {
        ObservableList<Sale> filteredSells = FXCollections.observableArrayList();
        ResultSet resultSet = databaseDriver.getFilteredSalesData(startDate, endDate, category);

        try {
            while (resultSet.next()) {
                int idVente = resultSet.getInt("id_vente");
                LocalDate dateVente = LocalDate.parse(resultSet.getString("date_vente"));
                String produit = resultSet.getString("produit");
                String categorie = resultSet.getString("categorie");
                int quantite = resultSet.getInt("quantite");
                float prixUnitaire = resultSet.getFloat("prix_unitaire");
                float total = resultSet.getFloat("total");

                Sale sell = new Sale(idVente, dateVente, produit, categorie, quantite, prixUnitaire, total);
                filteredSells.add(sell);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filteredSells;
    }

    // Calculate TotalSales
    public float calculateTotalSales(LocalDate startDate, LocalDate endDate, String category) {
        return databaseDriver.calculateTotalSales(startDate, endDate, category);
    }



    // Ventes par catégorie
    public ObservableList<String[]> getTotalSalesByCategory() {
        ObservableList<String[]> data = FXCollections.observableArrayList();
        try {
            ResultSet rs = databaseDriver.getTotalSalesByCategory();
            while (rs.next()) {
                data.add(new String[]{rs.getString("categorie"), String.format("%.2f", rs.getFloat("total_par_categorie"))});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Tendances mensuelles
    public ObservableList<String[]> getMonthlySalesTrends() {
        ObservableList<String[]> data = FXCollections.observableArrayList();
        try {
            ResultSet rs = databaseDriver.getMonthlySalesTrends();
            while (rs.next()) {
                data.add(new String[]{rs.getString("mois"), String.format("%.2f", rs.getFloat("total_par_mois"))});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Produits les plus vendus
    public ObservableList<String[]> getTopSellingProducts() {
        ObservableList<String[]> data = FXCollections.observableArrayList();
        try {
            ResultSet rs = databaseDriver.getTopSellingProducts();
            while (rs.next()) {
                data.add(new String[]{rs.getString("produit"), String.valueOf(rs.getInt("quantite_totale"))});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Produit le plus cher
    public String[] getMostExpensiveProduct() {
        try {
            ResultSet rs = databaseDriver.getMostExpensiveProduct();
            if (rs.next()) {
                return new String[]{rs.getString("produit"), String.format("%.2f", rs.getFloat("prix_max"))};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Produit le moins cher
    public String[] getLeastExpensiveProduct() {
        try {
            ResultSet rs = databaseDriver.getLeastExpensiveProduct();
            if (rs.next()) {
                return new String[]{rs.getString("produit"), String.format("%.2f", rs.getFloat("prix_min"))};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
