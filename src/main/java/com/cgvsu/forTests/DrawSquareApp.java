package com.cgvsu.forTests;

import io.github.shimeoki.jfx.rasterization.DDATriangler;
import io.github.shimeoki.jfx.rasterization.HTMLColorf;
import io.github.shimeoki.jfx.rasterization.Polygon3;
import io.github.shimeoki.jfx.rasterization.Vector2f;
import io.github.shimeoki.jfx.rasterization.triangle.Filler;
import io.github.shimeoki.jfx.rasterization.triangle.SolidFiller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DrawSquareApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Создаем канвас для рисования
        Canvas canvas = new Canvas(400, 400); // размеры канваса 400x400
        GraphicsContext gc = canvas.getGraphicsContext2D();


        DDATriangler triangler = new DDATriangler(gc);
        Vector2f v1 = new Vector2f(0,0);
        Vector2f v2 = new Vector2f(300,0);
        Vector2f v3 = new Vector2f(0,500);
        Polygon3 pol = new Polygon3(v1,v2,v3);
        SolidFiller filler = new SolidFiller(HTMLColorf.FUCHSIA);
        triangler.setFiller(filler);
        triangler.draw(pol);


        // Добавляем канвас в корневой узел
        StackPane root = new StackPane();
        root.getChildren().add(canvas);

        // Создаем сцену и отображаем её
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("Draw Square App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Метод для рисования квадрата.
     *
     * @param gc     Графический контекст
     * @param x      Координата X верхнего левого угла
     * @param y      Координата Y верхнего левого угла
     * @param size   Размер квадрата
     * @param color  Цвет квадрата
     */

    public static void main(String[] args) {
        launch(args);
    }
}
