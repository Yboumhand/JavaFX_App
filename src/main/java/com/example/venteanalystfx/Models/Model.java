package com.example.venteanalystfx.Models;

import com.example.venteanalystfx.Views.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    // here where i to embbed code
    private final ObservableList<Sell> sells;


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

    public ObservableList<Sell> getAllSells() {
        return sells;
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

                Sell sell = new Sell(idVente, dateVente, produit, categorie, quantite, prixUnitaire, total);
                // Bien que l'ID soit AI, il est utile de le récupérer pour d'éventuelles opérations futures
                // Si vous souhaitez stocker l'ID dans votre modèle Sell, vous devrez ajouter l'attribut et le constructeur correspondant.
                sells.add(sell);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
