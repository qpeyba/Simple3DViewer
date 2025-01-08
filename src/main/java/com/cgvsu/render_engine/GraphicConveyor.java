package com.cgvsu.render_engine;
//import javax.vecmath.*;
import io.github.alphameo.linear_algebra.mat.*;
import io.github.alphameo.linear_algebra.vec.*;

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

    //ВОЗМОЖНА ПРОБЛЕМА
    public static Matrix4 lookAt(Vector3 eye, Vector3 target, Vector3 up) {
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
                {-(Vec3Math.dot(resultX, eye)), -(Vec3Math.dot(resultY, eye)), -(Vec3Math.dot(resultZ, eye)), 1}}; // ?
//                -resultX.dot(eye), -resultY.dot(eye), -resultZ.dot(eye), 1}; -- старый вариант
        return new Mat4 (matrix);
    }

    public static Matrix4 perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        Matrix4 result = new Mat4();
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        result.set(0, 0, tangentMinusOnDegree / aspectRatio);
        result.set(1, 1, tangentMinusOnDegree);
        result.set(2, 2, (farPlane + nearPlane) / (farPlane - nearPlane));
        result.set(2, 3, 1.0F);
        result.set(3, 2, 2 * (nearPlane * farPlane) / (nearPlane - farPlane));

//        result.m00 = tangentMinusOnDegree / aspectRatio;
//        result.m11 = tangentMinusOnDegree;
//        result.m22 = (farPlane + nearPlane) / (farPlane - nearPlane);
//        result.m23 = 1.0F;
//        result.m32 = 2 * (nearPlane * farPlane) / (nearPlane - farPlane);
        return result;
    }

    // ВОЗМОЖНА ПРОБЛЕМА
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

    public static Vec2 vertexToPoint(final Vector3 vertex, final int width, final int height) {
        return new Vec2(vertex.x() * width + width / 2.0F, -vertex.y() * height + height / 2.0F);
    }
}
