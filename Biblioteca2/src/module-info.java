module Biblioteca2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    
    // Exporta o pacote principal para ser acessado pelo módulo javafx.graphics
    exports biblioteca.main to javafx.graphics;
    
    opens biblioteca.main to javafx.fxml;

    // Permite que o JavaFX acesse as classes do pacote control
    opens biblioteca.control to javafx.fxml;
}
