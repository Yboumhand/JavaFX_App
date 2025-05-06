module com.example.venteanalystfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // using sql connector to connect with our data base
    requires de.jensd.fx.glyphs.fontawesome;
    requires org.xerial.sqlitejdbc;

    opens com.example.venteanalystfx to javafx.fxml;
    exports com.example.venteanalystfx;
    exports com.example.venteanalystfx.Controllers;
    exports com.example.venteanalystfx.Controllers.User;
    exports com.example.venteanalystfx.Models;
    exports com.example.venteanalystfx.Views;
}