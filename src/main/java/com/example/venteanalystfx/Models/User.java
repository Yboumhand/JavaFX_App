package com.example.venteanalystfx.Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class User {
     private final StringProperty firstName;
     private final StringProperty lastName;
     private final StringProperty userName;


     public User(String fName, String lName, String uName) {
          this.firstName = new SimpleStringProperty(this, "prenom", fName);
          this.lastName = new SimpleStringProperty(this, "nom", lName);
          this.userName = new SimpleStringProperty(this, "nom_utilisateur", uName);
     }

     public StringProperty firstNameProperty() {
          return firstName;
     }

     public StringProperty lastNameProperty() {
          return lastName;
     }

     public StringProperty userNameProperty() {
          return userName;
     }


}
