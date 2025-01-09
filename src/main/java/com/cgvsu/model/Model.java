package com.cgvsu.model;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;

import java.util.*;

public class Model implements Cloneable {

    public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<Vector2f>();
    public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
    public ArrayList<Polygon> polygons = new ArrayList<Polygon>();


    public void triangulate() {
        ArrayList<Polygon> newPolygons = new ArrayList<Polygon>();

        for (Polygon polygon : polygons) {
            newPolygons.addAll(
                    polygon.triangulate()
            );
        }
        polygons = newPolygons;
    }


    public void computeNormals() {

        Map<Integer, Vector3f> vertexNormals = new HashMap<>();
        Map<Integer, Integer> vertexNormalsCount = new HashMap<>();


        for (Polygon polygon : polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            if (vertexIndices.size() < 3) {
                continue;
            }

            Vector3f v0 = vertices.get(vertexIndices.get(0));
            Vector3f v1 = vertices.get(vertexIndices.get(1));
            Vector3f v2 = vertices.get(vertexIndices.get(2));

            Vector3f edge1 = v1.subtract(v0);
            Vector3f edge2 = v2.subtract(v0);
            Vector3f faceNormal = edge1.cross(edge2).normalize();

            for (int index : vertexIndices) {
                vertexNormals.compute(index, (k, v) -> {
                    if (v == null) {
                        return faceNormal.copy();
                    } else {
                        return v.add(faceNormal);
                    }
                });
            }

            for (int index : vertexIndices) {

                if (vertexNormalsCount.containsKey(index)) {
                    vertexNormalsCount.put(index, vertexNormalsCount.get(index) + 1);
                } else {
                    vertexNormalsCount.put(index, 1);
                }

            }
        }


        for (Integer index : vertexNormals.keySet()) {
            vertexNormals.put(index, vertexNormals.get(index).divide(vertexNormalsCount.get(index)));
        }


        normals = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            normals.add(vertexNormals.getOrDefault(i, new Vector3f(0, 0, 0)));
        }
    }

    /** Для удаления вершин /VerDel **/
        public Model() {
    }


    // Метод клонирования вершин
    public ArrayList<Vector3f> cloneVertices() {
        ArrayList<Vector3f> clonedVertices = new ArrayList<>();
        for (Vector3f vertex : this.vertices) {
            clonedVertices.add(vertex.clone());
        }
        return clonedVertices;
    }

    // Метод клонирования текстурных вершин
    public ArrayList<Vector2f> cloneTextureVertices() {
        ArrayList<Vector2f> clonedTextureVertices = new ArrayList<>();
        for (Vector2f textureVertex : this.textureVertices) {
            clonedTextureVertices.add(textureVertex.clone());
        }
        return clonedTextureVertices;
    }

    // Метод клонирования нормалей
    public ArrayList<Vector3f> cloneNormals() {
        ArrayList<Vector3f> clonedNormals = new ArrayList<>();
        for (Vector3f normal : this.normals) {
            clonedNormals.add(normal.clone());
        }
        return clonedNormals;
    }

    // Метод клонирования полигонов
    public ArrayList<Polygon> clonePolygons() {
        ArrayList<Polygon> clonedPolygons = new ArrayList<>();
        for (Polygon polygon : this.polygons) {
            clonedPolygons.add(polygon.clone());
        }
        return clonedPolygons;
    }

    // Метод clone
    @Override
    public Model clone() {
        Model clonedModel = new Model();
        clonedModel.vertices = this.cloneVertices();
        clonedModel.textureVertices = this.cloneTextureVertices();
        clonedModel.normals = this.cloneNormals();
        clonedModel.polygons = this.clonePolygons();
        return clonedModel;
    }
    public void exportToOBJ() {
        // Устанавливаем локаль для использования точки как разделителя дробной части
        Locale.setDefault(Locale.US);

        // Вывод вершин
        for (Vector3f vertex : vertices) {
            System.out.printf("v %.6f %.6f %.6f%n", vertex.getX(), vertex.getY(), vertex.getZ());
        }

        // Вывод нормалей
        for (Vector3f normal : normals) {
            System.out.printf("vn %.6f %.6f %.6f%n", normal.getX(), normal.getY(), normal.getZ());
        }

        // Вывод текстурных координат
        for (Vector2f textureVertex : textureVertices) {
            System.out.printf("vt %.6f %.6f%n", textureVertex.getX(), textureVertex.getY());
        }

        // Вывод полигонов
        for (Polygon polygon : polygons) {
            System.out.print("f");
            for (int i = 0; i < polygon.getVertexIndices().size(); i++) {
                int vertexIndex = polygon.getVertexIndices().get(i) + 1; // Индексация в OBJ начинается с 1
                String facePart = String.valueOf(vertexIndex);

                if (!polygon.getTextureVertexIndices().isEmpty()) {
                    int textureIndex = polygon.getTextureVertexIndices().get(i) + 1;
                    facePart += "/" + textureIndex;
                }

                if (!polygon.getNormalIndices().isEmpty()) {
                    int normalIndex = polygon.getNormalIndices().get(i) + 1;
                    facePart += (facePart.contains("/") ? "" : "/") + "/" + normalIndex;
                }

                System.out.print(" " + facePart);
            }
            System.out.println();
        }
    }
    /** VerDel\ **/

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
