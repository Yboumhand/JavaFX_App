    package com.example.venteanalystfx.Models;

    // this file will connect to database and define the functions we want to use in the Model

    import java.sql.*;
    import java.time.LocalDate;

    public class DatabaseDriver {
        private Connection conn;


        public DatabaseDriver() {
            try {
                this.conn = DriverManager.getConnection("jdbc:sqlite:venteanalystfx.db");
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
        }


        public ResultSet getUserData(String username, String password) {
            Statement statement;
            ResultSet resultSet = null;
            try {
                statement = this.conn.createStatement();
                resultSet = statement.executeQuery("SELECT * FROM utilisateurs WHERE nom_utilisateur='"+username+"' AND mot_de_passe='"+password+"';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return resultSet;
        }

        // Getting All rows of the sales (ventes) table

        public ResultSet getAllSalesData() {
            Statement statement;
            ResultSet resultSet = null;
            try {
                statement = this.conn.createStatement();
                resultSet = statement.executeQuery("SELECT * FROM ventes;");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return resultSet;
        }

        public boolean insertSale(String product, String category, int quantity, float unitPrice, LocalDate saleDate) {
            PreparedStatement preparedStatement;
            try {
                // calculate the total
                float total = quantity * unitPrice;

                // Préparer la requête SQL
                String query = "INSERT INTO ventes (produit, categorie, quantite, prix_unitaire, total, date_vente) VALUES (?, ?, ?, ?, ?, ?)";
                preparedStatement = this.conn.prepareStatement(query);

                // Define values of parameters
                preparedStatement.setString(1, product);
                preparedStatement.setString(2, category);
                preparedStatement.setInt(3, quantity);
                preparedStatement.setFloat(4, unitPrice);
                preparedStatement.setFloat(5, total);
                preparedStatement.setString(6, saleDate.toString());

                // Execute the Query
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }


        public ResultSet getFilteredSalesData(LocalDate startDate, LocalDate endDate, String category) {
            PreparedStatement preparedStatement;
            ResultSet resultSet = null;
            try {
                String query;

                // Construire la requête SQL en fonction des filtres
                if (category != null && !category.equals("All")) {
                    query = "SELECT * FROM ventes WHERE date_vente BETWEEN ? AND ? AND categorie = ?";
                    preparedStatement = this.conn.prepareStatement(query);
                    preparedStatement.setString(1, startDate.toString());
                    preparedStatement.setString(2, endDate.toString());
                    preparedStatement.setString(3, category);
                } else {
                    query = "SELECT * FROM ventes WHERE date_vente BETWEEN ? AND ?";
                    preparedStatement = this.conn.prepareStatement(query);
                    preparedStatement.setString(1, startDate.toString());
                    preparedStatement.setString(2, endDate.toString());
                }

                resultSet = preparedStatement.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return resultSet;
        }

        // Méthode pour calculer le total des ventes selon les filtres
        public float calculateTotalSales(LocalDate startDate, LocalDate endDate, String category) {
            PreparedStatement preparedStatement;
            ResultSet resultSet = null;
            float totalSales = 0.0f;

            try {
                String query;

                // Construire la requête SQL en fonction des filtres
                if (category != null && !category.equals("All")) {
                    query = "SELECT SUM(total) as total_sum FROM ventes WHERE date_vente BETWEEN ? AND ? AND categorie = ?";
                    preparedStatement = this.conn.prepareStatement(query);
                    preparedStatement.setString(1, startDate.toString());
                    preparedStatement.setString(2, endDate.toString());
                    preparedStatement.setString(3, category);
                } else {
                    query = "SELECT SUM(total) as total_sum FROM ventes WHERE date_vente BETWEEN ? AND ?";
                    preparedStatement = this.conn.prepareStatement(query);
                    preparedStatement.setString(1, startDate.toString());
                    preparedStatement.setString(2, endDate.toString());
                }

                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    totalSales = resultSet.getFloat("total_sum");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return totalSales;
        }




        // Ventes totales par catégorie
        public ResultSet getTotalSalesByCategory() {
            ResultSet resultSet = null;
            try {
                String query = "SELECT categorie, SUM(total) as total_par_categorie FROM ventes GROUP BY categorie";
                Statement statement = conn.createStatement();
                resultSet = statement.executeQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return resultSet;
        }

        // Tendances mensuelles des ventes
        public ResultSet getMonthlySalesTrends() {
            ResultSet resultSet = null;
            try {
                String query = "SELECT strftime('%Y-%m', date_vente) AS mois, SUM(total) as total_par_mois FROM ventes GROUP BY mois ORDER BY mois";
                Statement statement = conn.createStatement();
                resultSet = statement.executeQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return resultSet;
        }

        // Produits les plus vendus (quantité)
        public ResultSet getTopSellingProducts() {
            ResultSet resultSet = null;
            try {
                String query = "SELECT produit, SUM(quantite) as quantite_totale FROM ventes GROUP BY produit ORDER BY quantite_totale DESC";
                Statement statement = conn.createStatement();
                resultSet = statement.executeQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return resultSet;
        }

        // most expansive product
        public ResultSet getMostExpensiveProduct() {
            ResultSet resultSet = null;
            try {
                String query = "SELECT produit, MAX(prix_unitaire) as prix_max FROM ventes";
                Statement statement = conn.createStatement();
                resultSet = statement.executeQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return resultSet;
        }

        // less expensive product
        public ResultSet getLeastExpensiveProduct() {
            ResultSet resultSet = null;
            try {
                String query = "SELECT produit, MIN(prix_unitaire) as prix_min FROM ventes";
                Statement statement = conn.createStatement();
                resultSet = statement.executeQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return resultSet;
        }

    }