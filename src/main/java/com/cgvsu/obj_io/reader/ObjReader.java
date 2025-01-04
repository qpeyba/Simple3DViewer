package com.cgvsu.obj_io.reader;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import io.github.shimeoki.jshaper.ObjFile;
import io.github.shimeoki.jshaper.obj.ModelReader;
import io.github.shimeoki.jshaper.obj.Reader;
import io.github.shimeoki.jshaper.obj.TextureVertex;
import io.github.shimeoki.jshaper.obj.Vertex;
import io.github.shimeoki.jshaper.obj.VertexNormal;
import io.github.shimeoki.jshaper.obj.Face;
import io.github.shimeoki.jshaper.ShaperError;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class ObjReader {
    public static Model read(String fileContent) {
        Model result = new Model();
        Reader reader = new ModelReader();
        
        try {
            File tempFile = File.createTempFile("model", ".obj");
            Files.writeString(tempFile.toPath(), fileContent);
            
            ObjFile objFile = reader.read(tempFile);
            
            for (Vertex vertex : objFile.vertexData().vertices()) {
                result.vertices.add(new Vector3f(
                    vertex.x(),
                    vertex.y(),
                    vertex.z()
                ));
            }
            
            for (TextureVertex texVertex : objFile.vertexData().textureVertices()) {
                result.textureVertices.add(new Vector2f(
                    texVertex.u(),
                    texVertex.v()
                ));
            }
            
            for (VertexNormal normal : objFile.vertexData().vertexNormals()) {
                result.normals.add(new Vector3f(
                    normal.i(),
                    normal.j(), 
                    normal.k()
                ));
            }
            
            for (Face face : objFile.elements().faces()) {
                Polygon polygon = new Polygon();
                ArrayList<Integer> vertexIndices = new ArrayList<>();
                ArrayList<Integer> textureIndices = new ArrayList<>();
                ArrayList<Integer> normalIndices = new ArrayList<>();
                
                for (var triplet : face.triplets()) {
                    if (triplet.vertex() != null) {
                        vertexIndices.add(objFile.vertexData().vertices().indexOf(triplet.vertex()));
                    }
                    if (triplet.textureVertex() != null) {
                        textureIndices.add(objFile.vertexData().textureVertices().indexOf(triplet.textureVertex()));
                    }
                    if (triplet.vertexNormal() != null) {
                        normalIndices.add(objFile.vertexData().vertexNormals().indexOf(triplet.vertexNormal()));
                    }
                }
                
                polygon.setVertexIndices(vertexIndices);
                polygon.setTextureVertexIndices(textureIndices); 
                polygon.setNormalIndices(normalIndices);
                result.polygons.add(polygon);
            }

            tempFile.delete();
            
            return result;
            
        } catch (ShaperError e) {
            throw new ObjReaderException(e.getMessage(), 0);
        } catch (Exception e) {
            throw new ObjReaderException("Failed to read OBJ file: " + e.getMessage(), 0);
        }
    }
}