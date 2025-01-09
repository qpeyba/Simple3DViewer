package com.cgvsu.render_engine;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class ZBuffer {

    private final float[][] depthBuffer;
    private final int width;
    private final int height;

    public ZBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.depthBuffer = new float[height][width];

        // Инициализируем Z-буфер значением максимальной глубины.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                depthBuffer[y][x] = Float.MAX_VALUE; // максимальная глубина для каждого пикселя
            }
        }
    }

    /**
     * Обновляет Z-буфер для пикселя, если новый пиксель ближе к камере (меньше значение глубины).
     *
     * @param x координата пикселя по оси X
     * @param y координата пикселя по оси Y
     * @param depth значение глубины пикселя
     * @param color цвет пикселя, если он должен быть отрисован
     * @param writer объект PixelWriter для рисования пикселей
     */
    public void writePixel(int x, int y, float depth, Color color, PixelWriter writer) {
        // Проверяем, если точка находится в пределах экрана
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }

        // Сравниваем глубину нового пикселя с текущим значением в Z-буфере
        if (depth < depthBuffer[y][x]) {
            // Если новый пиксель ближе, обновляем цвет и глубину
            writer.setColor(x, y, color);
            depthBuffer[y][x] = depth; // Обновляем значение глубины
        }
    }

    /**
     * Очищает Z-буфер.
     */
    public void clear() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                depthBuffer[y][x] = Float.MAX_VALUE;
            }
        }
    }

    /**
     * Получить значение глубины пикселя.
     *
     * @param x координата пикселя по оси X
     * @param y координата пикселя по оси Y
     * @return значение глубины пикселя
     */
    public float getDepth(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return Float.MAX_VALUE;
        }
        return depthBuffer[y][x];
    }
}

