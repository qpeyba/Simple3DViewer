package com.cgvsu.model;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import io.github.alphameo.linear_algebra.vec.*;

import java.util.ArrayList;
import java.util.Objects;

public class Model {

    public ArrayList<Vector3> vertices = new ArrayList<Vector3>();
    public ArrayList<Vector2> textureVertices = new ArrayList<Vector2>();
    public ArrayList<Vector3> normals = new ArrayList<Vector3>();
    public ArrayList<Polygon> polygons = new ArrayList<Polygon>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Model model)) return false;
        return Objects.equals(vertices, model.vertices) && Objects.equals(textureVertices, model.textureVertices) && Objects.equals(normals, model.normals) && Objects.equals(polygons, model.polygons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertices, textureVertices, normals, polygons);
    }
}
