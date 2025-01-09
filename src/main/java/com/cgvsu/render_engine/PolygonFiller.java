package com.cgvsu.render_engine;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Rasterization.DDATriangler;
import com.cgvsu.render_engine.Rasterization.ZBuffer;
import io.github.alphameo.linear_algebra.mat.Mat4;
import io.github.alphameo.linear_algebra.mat.*;
import io.github.alphameo.linear_algebra.vec.Vec2;
import io.github.alphameo.linear_algebra.vec.Vector2;
import io.github.alphameo.linear_algebra.vec.Vector3;
import javafx.scene.canvas.GraphicsContext;
import io.github.shimeoki.jfx.rasterization.*;
import io.github.shimeoki.jfx.rasterization.triangle.SolidFiller;
//import javax.vecmath.Matrix4f;
import static com.cgvsu.render_engine.GraphicConveyor.*;

public class PolygonFiller {
    public static void fillPolygon(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final Colorf colorf
    ) {

        Matrix4 modelMatrix = rotateScaleTranslate();
        Matrix4 viewMatrix = camera.getViewMatrix();
        Matrix4 projectionMatrix = camera.getProjectionMatrix();

        Matrix4 modelViewProjectionMatrix = new Mat4(projectionMatrix);
        modelViewProjectionMatrix = Mat4Math.prod(modelViewProjectionMatrix, viewMatrix);
        modelViewProjectionMatrix = Mat4Math.prod(modelViewProjectionMatrix, modelMatrix);

//        Matrix4f modelMatrix = rotateScaleTranslate();
//        Matrix4f viewMatrix = camera.getViewMatrix();
//        Matrix4f projectionMatrix = camera.getProjectionMatrix();
//
//        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
//        modelViewProjectionMatrix.mul(viewMatrix);
//        modelViewProjectionMatrix.mul(projectionMatrix);

        ZBuffer zBuffer = new ZBuffer(width, height);
        SolidFiller filler = new SolidFiller(colorf);

        final int nPolygons = mesh.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            Vector3 vertexVecmath1 = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(0));
            Vector3 vertexVecmath2 = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(1));
            Vector3 vertexVecmath3 = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(2));

            Vector2 resultPoint1 = vertexToVec2(multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath1), width, height);
            Vector2 resultPoint2 = vertexToVec2(multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath2), width, height);
            Vector2 resultPoint3 = vertexToVec2(multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath3), width, height);

            Vector2f v1 = new Vector2f(resultPoint1.x(), resultPoint1.y());
            Vector2f v2 = new Vector2f(resultPoint2.x(), resultPoint2.y());
            Vector2f v3 = new Vector2f(resultPoint3.x(), resultPoint3.y());
            Polygon3 poly = new Polygon3(v1, v2, v3);

            com.cgvsu.render_engine.Rasterization.DDATriangler triangler = new DDATriangler(graphicsContext, zBuffer, vertexVecmath1.z(), vertexVecmath2.z(), vertexVecmath3.z());
            triangler.setFiller(filler);
            triangler.draw(poly);

        }
    }
    public static Vector2 vertexToVec2(final Vector3 vertex, final int width, final int height) {
        return new Vec2(vertex.x() * width + width / 2.0F, -vertex.y() * height + height / 2.0F);
    }
}