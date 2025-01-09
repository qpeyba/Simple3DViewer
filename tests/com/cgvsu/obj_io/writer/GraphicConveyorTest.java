package com.cgvsu.obj_io.writer;

import com.cgvsu.render_engine.Camera;
import io.github.alphameo.linear_algebra.mat.Mat4;
import io.github.alphameo.linear_algebra.mat.Mat4Math;
import io.github.alphameo.linear_algebra.mat.Matrix4;
import io.github.alphameo.linear_algebra.vec.*;
import io.github.alphameo.linear_algebra.vec.Vector3;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class GraphicConveyorTest {
    @Test
    void lookAtTest(){
        Vector3 eye = new Vec3(0, 0, 100);
        Vector3 target = new Vec3(0, 0, 0);
        Vector3 up = new Vec3(0, 1.0f, 0);

        Assertions.assertTrue(Mat4Math.equals(
                Mat4Math.transpose(lookAt1(eye, target, up)),
                lookAt(eye, target, up)
        ));
    }

    @Test
    void perspectiveTest(){
        float fov = 1.0F;
        float aspectRatio = 1;
        float nearPlane = 0.01F;
        float farPlane = 100;
        Assertions.assertTrue(Mat4Math.equals(
                Mat4Math.transpose(perspective1(fov, aspectRatio, nearPlane, farPlane)),
                perspective(fov, aspectRatio, nearPlane, farPlane))
        );
    }

    //An old, correct variant of lookAt
    public static Matrix4 lookAt1(Vector3 eye, Vector3 target, Vector3 up) {
        Vector3 resultX = new Vec3();
        Vector3 resultY = new Vec3();
        Vector3 resultZ = new Vec3();

        resultZ = Vec3Math.sub(target, eye);
        resultX = Vec3Math.cross(up, resultZ);
        resultY = Vec3Math.cross(resultZ, resultX);

        Vec3Math.normalize(resultX);
        Vec3Math.normalize(resultY);
        Vec3Math.normalize(resultZ);

        float[][] matrix = new float[][]{
                {resultX.x(), resultY.x(), resultZ.x(), 0},
                {resultX.y(), resultY.y(), resultZ.y(), 0},
                {resultX.z(), resultY.z(), resultZ.z(), 0},
                {-Vec3Math.dot(resultX, eye), -Vec3Math.dot(resultY, eye), -Vec3Math.dot(resultZ, eye), 1}};
        return new Mat4(matrix);
    }

    public static Matrix4 perspective1(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        Matrix4 result = new Mat4();
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        result.set(0,0,tangentMinusOnDegree / aspectRatio);
        result.set(1, 1, tangentMinusOnDegree);
        result.set(2, 2, (farPlane + nearPlane) / (farPlane - nearPlane));
        result.set(2, 3, 1.0F);
        result.set(3, 2, 2 * (nearPlane * farPlane) / (nearPlane - farPlane));
        return result;
    }
}
