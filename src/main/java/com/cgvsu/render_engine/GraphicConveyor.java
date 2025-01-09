package com.cgvsu.render_engine;
import io.github.alphameo.linear_algebra.mat.*;
import io.github.alphameo.linear_algebra.vec.*;

import javax.vecmath.Point2f;

import static io.github.alphameo.linear_algebra.mat.Matrix4Col.*;
import static io.github.alphameo.linear_algebra.mat.Matrix4Row.*;

public class GraphicConveyor { // not to be used

    public static Matrix4 rotateScaleTranslate() {
        float[][] matrix = new float[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}};
        return new Mat4(matrix);
    }

    public static Matrix4 lookAt(Vector3 eye, Vector3 target) {
        return lookAt(eye, target, new Vec3(0F, 1.0F, 0F));
    }

    public static Matrix4 lookAt(Vector3 eye, Vector3 target, Vector3 up) {
//        Vector3 resultZ = Vec3Math.subtracted(target, eye);
//        Vector3 resultX = Vec3Math.cross(resultZ, up);
//        Vector3 resultY = Vec3Math.cross(resultX, resultZ);
        Vector3 resultZ = Vec3Math.subtracted(target, eye);
        Vector3 resultX = Vec3Math.cross(up, resultZ);
        Vector3 resultY = Vec3Math.cross(resultZ, resultX);

        Vec3Math.normalize(resultX);
        Vec3Math.normalize(resultY);
        Vec3Math.normalize(resultZ);

        return new Mat4(
                resultX.x(), resultX.y(), resultX.z(), -Vec3Math.dot(resultX, eye),
                resultY.x(), resultY.y(), resultY.z(), -Vec3Math.dot(resultY, eye),
                resultZ.x(), resultZ.y(), resultZ.z(), -Vec3Math.dot(resultZ, eye),
                0, 0, 0, 1
        );
    }

    public static Matrix4 perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        Matrix4 result = new Mat4();
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        result.set(R0, C0, tangentMinusOnDegree);
        result.set(R1, C1, tangentMinusOnDegree * 3 / aspectRatio);
        result.set(R2, C2, (farPlane + nearPlane) / (farPlane - nearPlane));
        result.set(R3, C2, 1.0F);
        result.set(R2, C3, 2 * (nearPlane * farPlane) / (nearPlane - farPlane));
        return result;
    }

//    public static Vec3 multiplyMatrix4ByVector3(final Matrix4 matrix, final Vector3 vertex) {
//        Mat4Math.transpose(matrix); //?
//        final float x = (vertex.x() * matrix.get(0, 0)) + (vertex.y() * matrix.get(1, 0)) + (vertex.z() * matrix.get(2, 0)) + matrix.get(3, 0);
//        final float y = (vertex.x() * matrix.get(0, 1)) + (vertex.y() * matrix.get(1, 1)) + (vertex.z() * matrix.get(2, 1)) + matrix.get(3, 1);
//        final float z = (vertex.x() * matrix.get(0, 2)) + (vertex.y() * matrix.get(1, 2)) + (vertex.z() * matrix.get(2, 2)) + matrix.get(3, 2);
//        final float w = (vertex.x() * matrix.get(0, 3)) + (vertex.y() * matrix.get(1, 3)) + (vertex.z() * matrix.get(2, 3)) + matrix.get(3, 3);
//        return new Vec3(x / w, y / w, z / w);
//    }
    public static Vector3 multiplyMatrix4ByVector3(final Matrix4 matrix, final Vector3 vertex) {
        Vector4 resVertex = Mat4Math.prod(matrix, Vec3Math.toVec4(vertex));
        Vec4Math.divide(resVertex, resVertex.w());
        return new Vec3(resVertex.x(), resVertex.y(), resVertex.z());
    }
    public static Vector2 vertexToVec2(final Vector3 vertex, final int width, final int height) {
        return new Vec2(vertex.x() * width + width / 2.0F, -vertex.y() * height + height / 2.0F);
    }

//    public static Point2f vertexToPoint(final Vector3 vertex, final int width, final int height) {
//        return new Point2f(vertex.x() * width + width / 2.0F, -vertex.y() * height + height / 2.0F);
//    }
}
