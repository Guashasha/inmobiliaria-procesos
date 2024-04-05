module org.example.codigoinmobiliaria {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens org.example.codigoinmobiliaria to javafx.fxml;
    exports org.example.codigoinmobiliaria;
}