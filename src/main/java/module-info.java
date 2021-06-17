module MultiPhase {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires org.jetbrains.annotations;
    requires DateTimeRCryptor;

    exports com.flowapp.MultiPhase;
    exports com.flowapp.MultiPhase.Models;
    exports com.flowapp.MultiPhase.Controllers to javafx.fxml;
    opens com.flowapp.MultiPhase;
    opens com.flowapp.MultiPhase.Controllers to javafx.fxml;
}