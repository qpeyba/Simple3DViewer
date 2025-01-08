package com.cgvsu.model;

import java.util.ArrayList;
import java.util.Objects;

public class Polygon implements Cloneable {

    private ArrayList<Integer> vertexIndices;
    private ArrayList<Integer> textureVertexIndices;
    private ArrayList<Integer> normalIndices;

    // public Polygon() {
    //     vertexIndices = new ArrayList<Integer>();
    //     textureVertexIndices = new ArrayList<Integer>(); // <-- РАСКОМЕННТИТЬ ЕСЛИ КОМПИЛЯТОР РУГАЕТСЯ НА ДЕЙСТВУЮЩИЙ КОНСТРУКТОР (НИЖЕ)
    //     normalIndices = new ArrayList<Integer>();
    // }


    /** Для удаления вершин /VerDel **/
    // Конструктор по умолчанию
    public Polygon() {
        vertexIndices = new ArrayList<>();
        textureVertexIndices = new ArrayList<>();
        normalIndices = new ArrayList<>();
    }

    // Метод clone
    @Override
    public Polygon clone() {
        try {
            Polygon clonedPolygon = (Polygon) super.clone();

            // Глубокое копирование списков
            clonedPolygon.vertexIndices = new ArrayList<>(this.vertexIndices);
            clonedPolygon.textureVertexIndices = new ArrayList<>(this.textureVertexIndices);
            clonedPolygon.normalIndices = new ArrayList<>(this.normalIndices);

            return clonedPolygon;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Этот код никогда не должен выполняться
        }
    }
    /** VerDel\ **/

    public void setVertexIndices(ArrayList<Integer> vertexIndices) {
        // assert vertexIndices.size() >= 3;
        this.vertexIndices = vertexIndices;
    }

    public void setTextureVertexIndices(ArrayList<Integer> textureVertexIndices) {
        // assert textureVertexIndices.size() >= 3;
        this.textureVertexIndices = textureVertexIndices;
    }

    public void setNormalIndices(ArrayList<Integer> normalIndices) {
        // assert normalIndices.size() >= 3;
        this.normalIndices = normalIndices;
    }

    public ArrayList<Integer> getVertexIndices() {
        return vertexIndices;
    }

    public ArrayList<Integer> getTextureVertexIndices() {
        return textureVertexIndices;
    }

    public ArrayList<Integer> getNormalIndices() {
        return normalIndices;
    }

    //метод триангуляции одного полигона
    public ArrayList<Polygon> triangulate() {
        int vertexNum = vertexIndices.size();
        ArrayList<Polygon> triangles = new ArrayList<>();

        if (vertexNum == 3) {
            triangles.add(this);
            return triangles;
        }

        for (int i = 1; i < vertexNum - 1; i++) {
            Polygon triangle = new Polygon();

            ArrayList<Integer> newVertexIndices = new ArrayList<>();
            newVertexIndices.add(vertexIndices.get(0));
            newVertexIndices.add(vertexIndices.get(i));
            newVertexIndices.add(vertexIndices.get(i + 1));
            triangle.setVertexIndices(newVertexIndices);

            if (!textureVertexIndices.isEmpty()) {
                ArrayList<Integer> newTextureIndices = new ArrayList<>();
                newTextureIndices.add(textureVertexIndices.get(0));
                newTextureIndices.add(textureVertexIndices.get(i));
                newTextureIndices.add(textureVertexIndices.get(i + 1));
                triangle.setTextureVertexIndices(newTextureIndices);
            }

            if (!normalIndices.isEmpty()) {
                ArrayList<Integer> newNormalIndices = new ArrayList<>();
                newNormalIndices.add(normalIndices.get(0));
                newNormalIndices.add(normalIndices.get(i));
                newNormalIndices.add(normalIndices.get(i + 1));
                triangle.setNormalIndices(newNormalIndices);
            }
            triangles.add(triangle);
        }
        return triangles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Polygon polygon)) return false;
        return Objects.equals(vertexIndices, polygon.vertexIndices) && Objects.equals(textureVertexIndices, polygon.textureVertexIndices) && Objects.equals(normalIndices, polygon.normalIndices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertexIndices, textureVertexIndices, normalIndices);
    }
}
