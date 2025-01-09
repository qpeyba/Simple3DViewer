module com.cgvsu {
    requires javafx.controls;
    requires javafx.fxml;
    requires vecmath;
    requires java.desktop;
    requires io.github.shimeoki.jshaper;
    requires javafx.graphics;
    requires transitive io.github.alphameo.linear_algebra;
    requires io.github.shimeoki.jfx.rasterization;
  

    opens com.cgvsu to javafx.fxml;
    exports com.cgvsu;
}