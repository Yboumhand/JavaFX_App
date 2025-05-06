package com.example.venteanalystfx.Models;

// this file will connect to database and define the functions we want to use in the Model

import java.sql.*;

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


}
