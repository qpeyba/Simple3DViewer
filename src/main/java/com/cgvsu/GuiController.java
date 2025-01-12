package com.cgvsu;

import com.cgvsu.math.affine.*;
import com.cgvsu.model.Polygon;
import com.cgvsu.render_engine.PolygonFiller;
import com.cgvsu.render_engine.RenderEngine;
import io.github.alphameo.linear_algebra.vec.Vec2;
import io.github.alphameo.linear_algebra.vec.Vec3;
import io.github.alphameo.linear_algebra.vec.Vector2;
import io.github.alphameo.linear_algebra.vec.Vector3;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
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
//import javax.vecmath.Vector3f;
import io.github.shimeoki.jfx.rasterization.Colorf;

import com.cgvsu.VertexDelete.Eraser;
import com.cgvsu.model.Model;
import com.cgvsu.obj_io.reader.ObjReader;
import com.cgvsu.obj_io.reader.ObjReaderException;
import com.cgvsu.obj_io.writer.ObjWriter;
import com.cgvsu.render_engine.Camera;

public class GuiController {

    final private float TRANSLATION = 2F;
  
    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private Model mesh = null;

    final private float ASPECT_RATIO = (float) Math.sqrt(2);
    // final private float ASPECT_RATIO = 16.0F / 9.0F;

    private Camera camera = new Camera(
            new Vec2((float)(Math.PI), (float)(-Math.PI/2)),
            100f,
            new Vec3(0, 0, 0),
            1.0F, ASPECT_RATIO, 0.01F, 100);

    private Timeline timeline;
    private Colorf color;

    private Vec3 currentTranslation = new Vec3(0, 0, 0);
    private Vec3 currentRotation = new Vec3(0, 0, 0);
    private Vec3 currentScale = new Vec3(1, 1, 1);

    @FXML
    private ListView<Model> listViewModels;
    private List<Model> models = new ArrayList<>();
    private Model currentModel;

    @FXML
    private ListView<Camera> listViewCameras;
    private List<Camera> cameras = new ArrayList<>();
    private Camera currentCamera;


    @FXML
    private void initialize() {
        canvas.setWidth(720 * ASPECT_RATIO);
        canvas.setHeight(720); 


        // 1.1
        // anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> {
        //         canvas.setWidth(newValue.doubleValue() );
        //         canvas.setHeight(newValue.doubleValue() / ASPECT_RATIO);
        // });
    
        //1.2
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> {
                canvas.setHeight(newValue.doubleValue());
                canvas.setWidth(newValue.doubleValue() * ASPECT_RATIO);
        });


        // 2 
        // anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> {
        //         double availableWidth = newValue.doubleValue();
        //         canvas.setWidth(availableWidth);
        //         canvas.setHeight((availableWidth) / ASPECT_RATIO);
        // });

        //3
        // anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        // anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        
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
                }            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();

        vertexRemoverButton.setOnAction(event -> handleVertexRemoval());

        updateTransformationFields();

        inputForModelMoving.setText("0.0 0.0 0.0");
        inputForModelRotation.setText("0.0 0.0 0.0");
        inputForModelScaling.setText("1.0 1.0 1.0");

        currentCamera = camera;
        cameras.add(camera);
        listViewCameras.setItems(FXCollections.observableArrayList(cameras));
    
        listViewCameras.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentCamera = newValue;
                camera = newValue;
            }
        });

        listViewCameras.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Camera item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("Camera " + (cameras.indexOf(item) + 1));
                }
            }
        });

        listViewModels.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Model item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("Model " + (models.indexOf(item) + 1));
                }
            }
        });

        listViewModels.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentModel = newValue;
                mesh = currentModel;
                updateTransformationFields();
            }
        });
    }

    private void updateTransformationFields() {
        inputForModelMoving.setText(String.format("%.1f %.1f %.1f", 
            currentTranslation.x(), currentTranslation.y(), currentTranslation.z()));
            
        inputForModelRotation.setText(String.format("%.1f %.1f %.1f",
            currentRotation.x(), currentRotation.y(), currentRotation.z()));
            
        inputForModelScaling.setText(String.format("%.1f %.1f %.1f",
            currentScale.x(), currentScale.y(), currentScale.z()));
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj")
        );
        fileChooser.setTitle("Load Model");
    
        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }
    
        Path fileName = Path.of(file.getAbsolutePath());
    
        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
        } catch (IOException e) {
            showError("File Error", "Failed to read file: " + e.getMessage());
        } catch (ObjReaderException e) {
            showError("Model Error", "Invalid model format: " + e.getMessage());
        } catch (Exception e) {
            showError("Error", "An unexpected error occurred: " + e.getMessage());
        }
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

        if (mesh == null) {
            showError("Save Error", "No model loaded to save");
            return;
        }
        try {
            ObjWriter writer = new ObjWriter();
            writer.write(mesh, file.getAbsolutePath());
        } catch (Exception e) {
            showError("Save Error", "Error saving file: " + e.getMessage());
        }
    }
    @FXML
    private TextField inputForModelMoving;

    @FXML
    private TextField inputForModelRotation;

    @FXML
    private TextField inputForModelScaling;

    private Vec3 accumulatedTranslation = new Vec3(0, 0, 0);
    private Vec3 accumulatedRotation = new Vec3(0, 0, 0);
    private Vec3 accumulatedScale = new Vec3(1, 1, 1);
    
    @FXML
    void applyTransformation(MouseEvent event) {
        if (currentModel == null) return;
    
        try {
            String[] translateValues = inputForModelMoving.getText().split("\\s+");
            String[] rotateValues = inputForModelRotation.getText().split("\\s+");
            String[] scaleValues = inputForModelScaling.getText().split("\\s+");
    
            Vec3 translation = new Vec3(
                Float.parseFloat(translateValues[0]),
                Float.parseFloat(translateValues[1]),
                Float.parseFloat(translateValues[2])
            );
    
            Vec3 rotation = new Vec3(
                Float.parseFloat(rotateValues[0]),
                Float.parseFloat(rotateValues[1]),
                Float.parseFloat(rotateValues[2])
            );
    
            Vec3 scale = new Vec3(
                Float.parseFloat(scaleValues[0]),
                Float.parseFloat(scaleValues[1]),
                Float.parseFloat(scaleValues[2])
            );
    
            Transformation transformation = new Transformation(
                new Translator(translation.x(), translation.y(), translation.z()),
                new Rotator(rotation.x(), Rotator.Axis.X),
                new Rotator(rotation.y(), Rotator.Axis.Y),
                new Rotator(rotation.z(), Rotator.Axis.Z),
                new Scaling(scale.x(), scale.y(), scale.z())
            );
    
            mesh.vertices = new ArrayList<>(transformation.transform(mesh.vertices));
    
            accumulatedTranslation = new Vec3(
                accumulatedTranslation.x() + translation.x(),
                accumulatedTranslation.y() + translation.y(),
                accumulatedTranslation.z() + translation.z()
            );
    
            accumulatedRotation = new Vec3(
                accumulatedRotation.x() + rotation.x(),
                accumulatedRotation.y() + rotation.y(),
                accumulatedRotation.z() + rotation.z()
            );
    
            accumulatedScale = new Vec3(
                accumulatedScale.x() * scale.x(),
                accumulatedScale.y() * scale.y(),
                accumulatedScale.z() * scale.z()
            );
    
            currentTranslation = translation;
            currentRotation = rotation;
            currentScale = scale;
    
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Invalid transformation values");
            alert.setContentText("Please enter valid numbers in format: x y z");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Transformation Error");
            alert.setHeaderText("Error applying transformation");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    void resetTransformation(MouseEvent event) {
        if (currentModel == null) return;
        
        Transformation resetTransformation = new Transformation(
            new Scaling(1/accumulatedScale.x(), 1/accumulatedScale.y(), 1/accumulatedScale.z()),
            new Rotator(-accumulatedRotation.z(), Rotator.Axis.Z),
            new Rotator(-accumulatedRotation.y(), Rotator.Axis.Y),
            new Rotator(-accumulatedRotation.x(), Rotator.Axis.X),
            new Translator(-accumulatedTranslation.x(), -accumulatedTranslation.y(), -accumulatedTranslation.z())
            );
        mesh.vertices = new ArrayList<>(resetTransformation.transform(mesh.vertices));
    
        currentTranslation = new Vec3(0, 0, 0);
        currentRotation = new Vec3(0, 0, 0);
        currentScale = new Vec3(1, 1, 1);
        accumulatedTranslation = new Vec3(0, 0, 0);
        accumulatedRotation = new Vec3(0, 0, 0);
        accumulatedScale = new Vec3(1, 1, 1);
    
        inputForModelMoving.setText("0.0 0.0 0.0");
        inputForModelRotation.setText("0.0 0.0 0.0");
        inputForModelScaling.setText("1.0 1.0 1.0");
    }
    
    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.moveDistance(-TRANSLATION);
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.moveDistance(TRANSLATION);
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.moveRotation(new Vec2(TRANSLATION/100, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.moveRotation(new Vec2(-TRANSLATION/100, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.moveRotation(new Vec2(0, TRANSLATION/100));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.moveRotation(new Vec2(0, -TRANSLATION/100));
    }

    @FXML
    public void moveCameraUp(ActionEvent actionEvent) {
        camera.moveTarget(new Vec3(0, TRANSLATION/2, 0));
    }
    @FXML
    public void moveCameraDown(ActionEvent actionEvent) {
        camera.moveTarget(new Vec3(0, -TRANSLATION/2, 0));
    }

    @FXML
    public void moveCameraLeft(ActionEvent actionEvent) {
        camera.moveTarget(new Vec3(-TRANSLATION/2, 0, 0));
    }
    @FXML
    public void moveCameraRight(ActionEvent actionEvent) {
        camera.moveTarget(new Vec3(TRANSLATION/2, 0, 0));
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

    private void handleVertexRemoval() {
        if (currentModel == null) {
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

            if (createNewModel) { // I dont quite understand what this crap does, probably it needs implementation of several models (I'll figure it out later)
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
    void addCamera(MouseEvent event) {
        Camera newCamera = new Camera(
            new Vec2((float)(Math.PI), (float)(-Math.PI/2)),
            100f,
            new Vec3(0, 0, 0),
            1.0F, 
            ASPECT_RATIO, 
            0.01F, 
            100
        );
        
        cameras.add(newCamera);
        listViewCameras.setItems(FXCollections.observableArrayList(cameras));
        listViewCameras.getSelectionModel().select(newCamera);
    }
    
    @FXML
    void removeCamera(MouseEvent event) {
        Camera selectedCamera = listViewCameras.getSelectionModel().getSelectedItem();
        
        if (selectedCamera == null) {
            showError("Error", "No camera selected");
            return;
        }
        
        if (cameras.size() <= 1) {
            showError("Error", "Cannot remove last camera");
            return;
        }
        
        cameras.remove(selectedCamera);
        listViewCameras.setItems(FXCollections.observableArrayList(cameras));
        
        if (selectedCamera == currentCamera) {
            listViewCameras.getSelectionModel().select(0);
        }
    }

    @FXML
    void addModel(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj")
        );
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            String fileContent = Files.readString(Path.of(file.getAbsolutePath()));
            Model newModel = ObjReader.read(fileContent);
            models.add(newModel);
            listViewModels.setItems(FXCollections.observableArrayList(models));
            listViewModels.getSelectionModel().select(newModel);
        } catch (Exception e) {
            showError("Model Loading Error", "Failed to load model: " + e.getMessage());
        }
    }

    @FXML
    void removeModel(MouseEvent event) {
        Model selectedModel = listViewModels.getSelectionModel().getSelectedItem();
        
        if (selectedModel == null) {
            showError("Error", "No model selected");
            return;
        }
        
        if (models.size() <= 1) {
            showError("Error", "Cannot remove last model");
            return;
        }
        
        models.remove(selectedModel);
        listViewModels.setItems(FXCollections.observableArrayList(models));
        
        if (selectedModel == currentModel) {
            listViewModels.getSelectionModel().select(0);
        }
    }
}