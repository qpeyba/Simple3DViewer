package com.cgvsu.render_engine;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;

//import com.cgvsu.math.Vector3f;
//import javafx.scene.canvas.GraphicsContext;
import com.cgvsu.math.affine.AffineTransformation;
import com.cgvsu.math.affine.Transformation;
import io.github.alphameo.linear_algebra.vec.*;
import io.github.alphameo.linear_algebra.mat.*;
import com.cgvsu.model.Model;
import javafx.scene.canvas.GraphicsContext;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height)
    {
        Matrix4 modelMatrix = new Transformation().getMatrix();
        Matrix4 viewMatrix = camera.getViewMatrix();
        Matrix4 projectionMatrix = camera.getProjectionMatrix();

        Matrix4 modelViewProjectionMatrix = new Mat4(projectionMatrix);
        modelViewProjectionMatrix = Mat4Math.prod(modelViewProjectionMatrix, viewMatrix);
        modelViewProjectionMatrix = Mat4Math.prod(modelViewProjectionMatrix, modelMatrix);
//        modelViewProjectionMatrix.mul(viewMatrix);
//        modelViewProjectionMatrix.mul(projectionMatrix);

        final int nPolygons = mesh.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();

            ArrayList<Vector2> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                Vector3 vertex = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));

                Vector3 vertexVecmath = new Vec3(vertex.x(), vertex.y(), vertex.y());

                Vector2 resultPoint = vertexToPoint(multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath), width, height);
                resultPoints.add(resultPoint);
            }

            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                graphicsContext.strokeLine(
                        resultPoints.get(vertexInPolygonInd - 1).x(),
                        resultPoints.get(vertexInPolygonInd - 1).y(),
                        resultPoints.get(vertexInPolygonInd).x(),
                        resultPoints.get(vertexInPolygonInd).y());
            }

            if (nVerticesInPolygon > 0)
                graphicsContext.strokeLine(
                        resultPoints.get(nVerticesInPolygon - 1).x(),
                        resultPoints.get(nVerticesInPolygon - 1).y(),
                        resultPoints.get(0).x(),
                        resultPoints.get(0).y());
        }
    }
}