package com.cgvsu.render_engine;

import com.cgvsu.math.affine.AffineTransformation;
import com.cgvsu.math.affine.Transformation;
import com.cgvsu.model.Model;
import io.github.alphameo.linear_algebra.mat.Mat4;
import io.github.alphameo.linear_algebra.mat.Mat4Math;
import io.github.alphameo.linear_algebra.mat.Matrix4;
import io.github.alphameo.linear_algebra.vec.*;
import javafx.scene.canvas.GraphicsContext;

import static com.cgvsu.render_engine.GraphicConveyor.vertexToPoint;

public class RenderEngine {
    private Vector3[] resultPoints;

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height)
    {
        AffineTransformation modelMatrix = new Transformation(); // матрица трансформации
        Matrix4 viewMatrix = camera.getViewMatrix(); // матрица камеры
        Matrix4 projectionMatrix = camera.getProjectionMatrix(); // матрица проекции

//        Matrix4 modelMatrix = rotateScaleTranslate();
//        Matrix4f viewMatrix = camera.getViewMatrix();
//        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4 modelViewProjectionMatrix = new Mat4(modelMatrix.getMatrix());
        Mat4Math.prod(modelViewProjectionMatrix, viewMatrix);
        Mat4Math.prod(modelViewProjectionMatrix,projectionMatrix); // проекция модели

        Vector3[] resultPoints = new Vec3[mesh.vertices.size()];
        Vector4 vertex4;
        Vector3 vertex, projectedVertex;
        Vector2 resultPoint;
        for (int i = 0; i < mesh.vertices.size(); i++) {
            vertex = mesh.vertices.get(i);

            // project vertices
            vertex4 = Mat4Math.prod(modelViewProjectionMatrix,
                    new Vec4(vertex.x(), vertex.y(), vertex.z(), 1.0f)
            );
            projectedVertex = new Vec3(
                    vertex4.x() / vertex4.w(),
                    vertex4.y() / vertex4.w(),
                    vertex4.z() / vertex4.w()
            );
            resultPoint = vertexToPoint(projectedVertex, width, height);
            resultPoints[i] = new Vec3(resultPoint.x(), resultPoint.y(), projectedVertex.z());

//            //project normals
//            normal4 = modelMatrix.multiplyMV(new Vector4f(normal.x(), normal.y(), normal.z(), 1.0f));
//            normals[i] = new Vector3f(normal4.x(), normal4.y(), normal4.z());
        }
    }
}