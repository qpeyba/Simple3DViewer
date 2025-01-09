package com.cgvsu.math;

import io.github.alphameo.linear_algebra.vec.Vec2;
import io.github.alphameo.linear_algebra.vec.Vec3;
import io.github.alphameo.linear_algebra.vec.Vector2;
import io.github.alphameo.linear_algebra.vec.Vector3;

import java.util.Objects;

public class Vector2f implements Cloneable {
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    float x, y;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    /** Для удаления вершин /VerDel **/
    @Override
    public Vector2f clone()  {
        try {
            return (Vector2f) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    public static Vector2 cloneVector2(Vector2 vector) {
        return new Vec2(vector.x(), vector.y());
    }
    /** VerDel\ **/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector2f vector2f)) return false;
        return Float.compare(x, vector2f.x) == 0 && Float.compare(y, vector2f.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
