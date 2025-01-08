package com.cgvsu.math;

import java.util.Objects;

public class Vector3f implements Cloneable {
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Vector3f other) {
        final float eps = 1e-7f;
        return Math.abs(x - other.x) < eps && Math.abs(y - other.y) < eps && Math.abs(z - other.z) < eps;
    }

    float x, y, z;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    /**
     * Для удаления вершин /VerDel
     **/
    @Override
    public Vector3f clone() {
        try {
            return (Vector3f) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * VerDel\
     **/

    //методы ниже используются для вычисления нормалей модели
    //a = (a1, a2, a3)  b = (b1, b2, b3)
    //a × b = (a2 * b3 - a3 * b2, a3 * b1 - a1 * b3, a1 * b2 - a2 * b1)
    public Vector3f cross(Vector3f other) {
        return new Vector3f(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }

    public Vector3f normalize() {
        float length = (float) Math.sqrt(x * x + y * y + z * z);
        return new Vector3f(x / length, y / length, z / length);
    }

    public Vector3f add(Vector3f other) {
        return new Vector3f(x + other.x, y + other.y, z + other.z);
    }

    public Vector3f subtract(Vector3f other) {
        return new Vector3f(x - other.x, y - other.y, z - other.z);
    }

    public Vector3f multiply(float scalar) {
        return new Vector3f(x * scalar, y * scalar, z * scalar);
    }

    public Vector3f divide(float scalar) {
        return new Vector3f(x / scalar, y / scalar, z / scalar);
    }

    public Vector3f copy() {
        return new Vector3f(this.x, this.y, this.z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector3f vector3f)) return false;
        return Float.compare(x, vector3f.x) == 0 && Float.compare(y, vector3f.y) == 0 && Float.compare(z, vector3f.z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", x, y, z);
    }
}
