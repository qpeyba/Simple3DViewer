module com.cgvsu {
    requires javafx.controls;
    requires javafx.fxml;
    requires vecmath;
    requires java.desktop;
    requires io.github.shimeoki.jshaper;
    requires javafx.graphics;


    opens com.cgvsu to javafx.fxml;
    exports com.cgvsu;
}