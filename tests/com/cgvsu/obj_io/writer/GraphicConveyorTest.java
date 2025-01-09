package com.cgvsu.obj_io.writer;

import com.cgvsu.render_engine.Camera;
import io.github.alphameo.linear_algebra.mat.Mat4;
import io.github.alphameo.linear_algebra.mat.Mat4Math;
import io.github.alphameo.linear_algebra.mat.Matrix4;
import io.github.alphameo.linear_algebra.vec.*;
import io.github.alphameo.linear_algebra.vec.Vector3;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import javax.vecmath.*;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class GraphicConveyorTest {
    @Test
    void lookAtTest(){
        Vector3 eye = new Vec3(0, 0, 100);
        Vector3 target = new Vec3(0, 0, 0);
        Vector3 up = new Vec3(0, 1.0f, 0);
//        Matrix4f mat = lookAt2(
//                new Vector3f(0, 0, 100),
//                new Vector3f(0, 0, 0),
//                new Vector3f(0, 1.0f, 0)
//        );
//        System.out.println(mat.m00);

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
    @Test
    void multiplyMatrix4ByVector3Test(){
        Matrix4 matrix = new Mat4(0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f, 11.0f, 12.0f, 13.0f, 14.0f, 15.0f);
        Vector3 vector3 = new Vec3 (3, 6, 4);

        Assertions.assertTrue(Vec3Math.equals(
                multiplyMatrix4ByVector3(matrix, vector3),
                multiplyMatrix4ByVector3_v2(matrix, vector3)
        ));

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
    public static Matrix4f lookAt2(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f resultX = new Vector3f();
        Vector3f resultY = new Vector3f();
        Vector3f resultZ = new Vector3f();

        resultZ.sub(target, eye);
        resultX.cross(up, resultZ);
        resultY.cross(resultZ, resultX);

        resultX.normalize();
        resultY.normalize();
        resultZ.normalize();

        float[] matrix = new float[]{
                resultX.x, resultY.x, resultZ.x, 0,
                resultX.y, resultY.y, resultZ.y, 0,
                resultX.z, resultY.z, resultZ.z, 0,
                -resultX.dot(eye), -resultY.dot(eye), -resultZ.dot(eye), 1};
        return new Matrix4f(matrix);
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
        public static Vec3 multiplyMatrix4ByVector3_v2(final Matrix4 matrix, final Vector3 vertex) {
        Mat4Math.transpose(matrix); //?
        final float x = (vertex.x() * matrix.get(0, 0)) + (vertex.y() * matrix.get(1, 0)) + (vertex.z() * matrix.get(2, 0)) + matrix.get(3, 0);
        final float y = (vertex.x() * matrix.get(0, 1)) + (vertex.y() * matrix.get(1, 1)) + (vertex.z() * matrix.get(2, 1)) + matrix.get(3, 1);
        final float z = (vertex.x() * matrix.get(0, 2)) + (vertex.y() * matrix.get(1, 2)) + (vertex.z() * matrix.get(2, 2)) + matrix.get(3, 2);
        final float w = (vertex.x() * matrix.get(0, 3)) + (vertex.y() * matrix.get(1, 3)) + (vertex.z() * matrix.get(2, 3)) + matrix.get(3, 3);
        return new Vec3(x / w, y / w, z / w);
    }
}
