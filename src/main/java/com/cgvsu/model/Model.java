package com.cgvsu.model;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import io.github.alphameo.linear_algebra.vec.*;

import java.util.*;

import static com.cgvsu.math.Vector2f.cloneVector2;
import static com.cgvsu.math.Vector3f.*;

public class Model implements Cloneable {

    public ArrayList<Vector3> vertices = new ArrayList<Vector3>();
    public ArrayList<Vector2> textureVertices = new ArrayList<Vector2>();
    public ArrayList<Vector3> normals = new ArrayList<Vector3>();
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

        Map<Integer, Vector3> vertexNormals = new HashMap<>();
        Map<Integer, Integer> vertexNormalsCount = new HashMap<>();


        for (Polygon polygon : polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            if (vertexIndices.size() < 3) {
                continue;
            }

            Vector3 v0 = vertices.get(vertexIndices.get(0));
            Vector3 v1 = vertices.get(vertexIndices.get(1));
            Vector3 v2 = vertices.get(vertexIndices.get(2));

            Vector3 edge1 = Vec3Math.sub(v1, v0);
            Vector3 edge2 = Vec3Math.sub(v2, v0);
            Vector3 faceNormal = Vec3Math.normalize(Vec3Math.cross(edge1, edge2));
//            Vector3 edge1 = v1.subtract(v0);
//            Vector3 edge2 = v2.subtract(v0);
//            Vector3 faceNormal = edge1.cross(edge2).normalize();

            for (int index : vertexIndices) {
                vertexNormals.compute(index, (k, v) -> {
                    if (v == null) {
                        return cloneVector3(faceNormal);
//                        return faceNormal.copy();
                    } else {
                        return Vec3Math.add(v, faceNormal);
//                        return v.add(faceNormal);
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
            vertexNormals.put(index, Vec3Math.divide(vertexNormals.get(index), vertexNormalsCount.get(index)));
//            vertexNormals.put(index, vertexNormals.get(index).divide(vertexNormalsCount.get(index)));
        }


        normals = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            normals.add(vertexNormals.getOrDefault(i, new Vec3(0, 0, 0)));
        }
    }


    /** Для удаления вершин /VerDel **/
        public Model() {
    }


    // Метод клонирования вершин
    public ArrayList<Vector3> cloneVertices() {
        ArrayList<Vector3> clonedVertices = new ArrayList<>();
        for (Vector3 vertex : this.vertices) {
            clonedVertices.add(cloneVector3(vertex));
        }
        return clonedVertices;
    }

    // Метод клонирования текстурных вершин
    public ArrayList<Vector2> cloneTextureVertices() {
        ArrayList<Vector2> clonedTextureVertices = new ArrayList<>();
        for (Vector2 textureVertex : this.textureVertices) {
            clonedTextureVertices.add(cloneVector2(textureVertex));
        }
        return clonedTextureVertices;
    }

    // Метод клонирования нормалей
    public ArrayList<Vector3> cloneNormals() {
        ArrayList<Vector3> clonedNormals = new ArrayList<>();
        for (Vector3 normal : this.normals) {
            clonedNormals.add(cloneVector3(normal));
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
        for (Vector3 vertex : vertices) {
            System.out.printf("v %.6f %.6f %.6f%n", vertex.x(), vertex.x(), vertex.x());
        }

        // Вывод нормалей
        for (Vector3 normal : normals) {
            System.out.printf("vn %.6f %.6f %.6f%n", normal.x(), normal.x(), normal.x());
        }

        // Вывод текстурных координат
        for (Vector2 textureVertex : textureVertices) {
            System.out.printf("vt %.6f %.6f%n", textureVertex.x(), textureVertex.x());
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
