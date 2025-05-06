package com.example.venteanalystfx.Models;


import javafx.beans.property.*;
import java.time.LocalDate;


public class Sell {

    private final IntegerProperty idVente;
    private final ObjectProperty<LocalDate> dateVente;
    private final StringProperty produit;
    private final StringProperty categorie;
    private final IntegerProperty quantite;
    private final FloatProperty prixUnitaire;
    private final FloatProperty total;

    public Sell(int idVente, LocalDate date, String product, String category, int quantity, float unitPrice, float totalAmount) {
        this.idVente = new SimpleIntegerProperty(this, "idVente", idVente);
        this.dateVente = new SimpleObjectProperty<>(this, "date_vente", date);
        this.produit = new SimpleStringProperty(this, "produit", product);
        this.categorie = new SimpleStringProperty(this, "categorie", category);
        this.quantite = new SimpleIntegerProperty(this, "quantite", quantity);
        this.prixUnitaire = new SimpleFloatProperty(this, "prix_unitaire", unitPrice);
        this.total = new SimpleFloatProperty(this, "total", totalAmount);
    }

    // Getters pour les propriétés (pour la liaison de données dans JavaFX)

    public IntegerProperty idVenteProperty() {
        return idVente;
    }

    public ObjectProperty<LocalDate> dateVenteProperty() {
        return dateVente;
    }

    public StringProperty produitProperty() {
        return produit;
    }

    public StringProperty categorieProperty() {
        return categorie;
    }

    public IntegerProperty quantiteProperty() {
        return quantite;
    }

    public FloatProperty prixUnitaireProperty() {
        return prixUnitaire;
    }

    public FloatProperty totalProperty() {
        return total;
    }


    // Setters pour les valeurs (facultatif selon vos besoins)

    public void setIdVente(int idVente) {
        this.idVente.set(idVente);
    }

    public void setDateVente(LocalDate date) {
        this.dateVente.set(date);
    }

    public void setProduit(String product) {
        this.produit.set(product);
    }

    public void setCategorie(String category) {
        this.categorie.set(category);
    }

    public void setQuantite(int quantity) {
        this.quantite.set(quantity);
    }

    public void setPrixUnitaire(float unitPrice) {
        this.prixUnitaire.set(unitPrice);
    }

    public void setTotal(float totalAmount) {
        this.total.set(totalAmount);
    }




}
