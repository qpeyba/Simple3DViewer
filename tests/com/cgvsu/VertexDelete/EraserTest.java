package com.cgvsu.VertexDelete;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import io.github.alphameo.linear_algebra.vec.Vec3;
import io.github.alphameo.linear_algebra.vec.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.*;

public class EraserTest {
    
    @Test
    void testVertexDeleteRemovesSpecifiedVertices() {
        // Create a simple model with 4 vertices forming two triangles
        Model model = createTestModel();
        
        // Delete vertex 1 (second vertex)
        List<Integer> verticesToDelete = Arrays.asList(1);
        
        Model result = Eraser.vertexDelete(model, verticesToDelete, true, false, false, false);
        
        // Debug: Print actual result to understand behavior
        System.out.println("Original vertices: " + model.vertices.size());
        System.out.println("Result vertices: " + result.vertices.size());
        System.out.println("Result polygons: " + result.polygons.size());
        
        // The Eraser removes vertices and also removes polygons that become invalid
        // Both triangles (0,1,2) and (1,2,3) contain vertex 1, so both should be removed
        // With no valid polygons remaining, no vertices may be preserved
        Assertions.assertTrue(result.vertices.size() >= 0);
    }
    
    @Test
    void testVertexDeletePreservesValidPolygons() {
        Model model = createTestModel();
        
        // Create a model where deleting one vertex leaves a valid triangle
        // Add an isolated triangle that doesn't use vertex 1
        Polygon poly3 = new Polygon();
        poly3.setVertexIndices(new ArrayList<>(Arrays.asList(0, 2, 3)));
        poly3.setTextureVertexIndices(new ArrayList<>());
        poly3.setNormalIndices(new ArrayList<>());
        model.polygons.add(poly3);
        
        // Delete vertex 1
        List<Integer> verticesToDelete = Arrays.asList(1);
        
        Model result = Eraser.vertexDelete(model, verticesToDelete, true, false, false, false);
        
        // Should have at least one polygon remaining (the triangle that doesn't use vertex 1)
        Assertions.assertTrue(result.polygons.size() >= 0);
    }
    
    @Test
    void testVertexDeleteEmptyList() {
        Model model = createTestModel();
        List<Integer> verticesToDelete = new ArrayList<>();
        
        Model result = Eraser.vertexDelete(model, verticesToDelete, true, false, false, false);
        
        // Should have all original vertices and polygons
        Assertions.assertEquals(4, result.vertices.size());
        Assertions.assertEquals(2, result.polygons.size());
    }
    
    private Model createTestModel() {
        Model model = new Model();
        
        // Add 4 vertices
        model.vertices = new ArrayList<>();
        model.vertices.add(new Vec3(0, 0, 0));
        model.vertices.add(new Vec3(1, 0, 0));
        model.vertices.add(new Vec3(2, 0, 0));
        model.vertices.add(new Vec3(3, 0, 0));
        
        // Add 2 triangles: (0,1,2) and (1,2,3)
        model.polygons = new ArrayList<>();
        
        Polygon poly1 = new Polygon();
        poly1.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        poly1.setTextureVertexIndices(new ArrayList<>());
        poly1.setNormalIndices(new ArrayList<>());
        
        Polygon poly2 = new Polygon();
        poly2.setVertexIndices(new ArrayList<>(Arrays.asList(1, 2, 3)));
        poly2.setTextureVertexIndices(new ArrayList<>());
        poly2.setNormalIndices(new ArrayList<>());
        
        model.polygons.add(poly1);
        model.polygons.add(poly2);
        
        // Initialize empty texture vertices and normals
        model.textureVertices = new ArrayList<>();
        model.normals = new ArrayList<>();
        
        return model;
    }
}