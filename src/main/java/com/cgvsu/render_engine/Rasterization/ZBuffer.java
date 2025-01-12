package com.cgvsu.render_engine.Rasterization;

import java.util.Arrays;

public class ZBuffer {

    private final int width;
    private final int height;
    private final float[][] zBuffer;

    public ZBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.zBuffer = new float[width][height];
        for (int x = 0; x < width; x++) {
            Arrays.fill(zBuffer[x], Float.MAX_VALUE);
        }
    }
    public boolean shouldDraw(int x, int y, float z) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }
        if (z < zBuffer[x][y]) {
            zBuffer[x][y] = z;
            return true;
        }
        return false;
    }
    public float getDepth(int x, int y) {
        return zBuffer[x][y];
    }
}