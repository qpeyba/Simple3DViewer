package com.cgvsu;

import com.cgvsu.model.Polygon;
import com.cgvsu.render_engine.PolygonFiller;
import com.cgvsu.render_engine.RenderEngine;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.File;
import javax.vecmath.Vector3f;

import com.cgvsu.VertexDelete.Eraser;
import com.cgvsu.model.Model;
import com.cgvsu.obj_io.reader.ObjReader;
import com.cgvsu.obj_io.writer.ObjWriter;
import com.cgvsu.render_engine.Camera;
import io.github.shimeoki.jfx.rasterization.Colorf;

public class GuiController {

    final private float TRANSLATION = 0.5F;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private Model mesh = null;

    private Camera camera = new Camera(
            new Vector3f(0, 00, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;
    private Colorf color;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            if (mesh != null) {
                if (color!=null){
                    PolygonFiller.fillPolygon(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height, color);
                }else{
                    RenderEngine.render(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height);
                }
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();

        vertexRemoverButton.setOnAction(event -> handleVertexRemoval());
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }

    @FXML
    private void onSaveModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj")
        );
        fileChooser.setTitle("Save Model");
    
        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }
    
        try {
            ObjWriter writer = new ObjWriter();
            writer.write(mesh, file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
    @FXML
    private TextField VerticesToRemove;
    @FXML
    private Button vertexRemoverButton;
    @FXML
    private CheckBox checkboxNewFile;
    @FXML
    private CheckBox checkboxRemoveNormals;
    @FXML
    private CheckBox checkboxRemoveTextures;
    @FXML
    private CheckBox checkboxRemovePolygons;

    private void handleVertexRemoval() {
        if (mesh == null) {
            showError("No model loaded", "Please load a model first before trying to remove vertices.");
            return;
        }

        try {
            List<Integer> verticesToRemove = parseVerticesToRemove(VerticesToRemove.getText());
            
            if (!validateVertexIndices(verticesToRemove)) {
                showError("Invalid vertices", "One or more vertex indices are out of range.");
                return;
            }

            boolean createNewModel = checkboxNewFile.isSelected();
            boolean removeNormals = !checkboxRemoveNormals.isSelected();
            boolean removeTextures = !checkboxRemoveTextures.isSelected();
            boolean removePolygons = !checkboxRemovePolygons.isSelected();

            Model resultModel = Eraser.vertexDelete(
                mesh,
                verticesToRemove,
                createNewModel,
                removeNormals,
                removeTextures,
                removePolygons
            );

            if (createNewModel) { // I dont quite understand what this shit does, probably it needs implementation of several models (I'll figure it out later)
                mesh = resultModel;
            }

        } catch (NumberFormatException e) {
            showError("Invalid input", "Please enter valid vertex indices (comma-separated numbers).");
        } catch (Exception e) {
            showError("Error", "An error occurred while removing vertices: " + e.getMessage());
        }
    }

    private List<Integer> parseVerticesToRemove(String input) {
        List<Integer> indices = new ArrayList<>();
        String[] parts = input.split(",");
        for (String part : parts) {
            indices.add(Integer.parseInt(part.trim()));
        }
        return indices;
    }

    private boolean validateVertexIndices(List<Integer> indices) {
        int maxIndex = mesh.vertices.size() - 1;
        for (Integer index : indices) {
            if (index < 0 || index > maxIndex) {
                return false;
            }
        }
        return true;
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private Button colorPolygonButton;

    @FXML
    private ComboBox<String> colorPicker;

//    @FXML
//    private void handleColorPolygon(ActionEvent actionEvent) {
//
//        if (mesh == null) {
//            showError("No model loaded", "Please load a model first before applying colors.");
//            return;
//        }
//
//        String selectedColor = colorPicker.getValue();
//        if (selectedColor == null) {
//            showError("No color selected", "Please select a color to apply.");
//            return;
//        }
//
//        switch (selectedColor.toLowerCase()) {
//            case "red":
//                color = new Colorf(1.0f, 0.0f, 0.0f, 1.0F);
//                break;
//            case "green":
//                color = new Colorf(0.0f, 1.0f, 0.0f, 1.0F);
//                break;
//            case "blue":
//                color = new Colorf(0.0f, 0.0f, 1.0f, 1.0F);
//                break;
//            case "yellow":
//                color = new Colorf(1.0f, 1.0f, 0.0f, 1.0F);
//                break;
//            case "white":
//                color = new Colorf(1.0f, 1.0f, 1.0f, 1.0F);
//                break;
//            default:
//                color = new Colorf(0.0f, 0.0f, 0.0f, 1.0F); // Черный цвет по умолчанию
//                break;
//        }
//
//        timeline = new Timeline();
//        timeline.setCycleCount(Animation.INDEFINITE);
//
//        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
//            double width = canvas.getWidth();
//            double height = canvas.getHeight();
//
//            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
//            camera.setAspectRatio((float) (width / height));
//
//            if (mesh != null) {
//                PolygonFiller.fillPolygon(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height, color);
//            }
//        });
//
//        timeline.getKeyFrames().add(frame);
//        timeline.play();
//
//        vertexRemoverButton.setOnAction(event -> handleVertexRemoval());
//    }

    public void chooseColor(ActionEvent actionEvent){
        String selectedColor = colorPicker.getValue();
        if (selectedColor == null) {
            showError("No color selected", "Please select a color to apply.");
            return;
        }

        switch (selectedColor.toLowerCase()) {
            case "red":
                color = new Colorf(1.0f, 0.0f, 0.0f, 1.0F);
                break;
            case "green":
                color = new Colorf(0.0f, 1.0f, 0.0f, 1.0F);
                break;
            case "blue":
                color = new Colorf(0.0f, 0.0f, 1.0f, 1.0F);
                break;
            case "yellow":
                color = new Colorf(1.0f, 1.0f, 0.0f, 1.0F);
                break;
            case "white":
                color = new Colorf(1.0f, 1.0f, 1.0f, 1.0F);
                break;
            default:
                color = new Colorf(0.0f, 0.0f, 0.0f, 1.0F); // Черный цвет по умолчанию
                break;
        }
    }
    public void resetPolygonColor(ActionEvent actionEvent) {
        color = null;
    }
}