package com.cgvsu.render_engine.Rasterization;

import io.github.shimeoki.jfx.rasterization.*;
import io.github.shimeoki.jfx.rasterization.triangle.Barycentrics;
import io.github.shimeoki.jfx.rasterization.triangle.Barycentricser;
import io.github.shimeoki.jfx.rasterization.triangle.Filler;
import io.github.shimeoki.jfx.rasterization.triangle.SolidFiller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;


public final class DDATriangler implements Triangler {
    private final PixelWriter writer;
    private Filler filler;
    private Colorf color;
    private final Barycentricser barycentricser;
    private final Barycentrics barycentrics;
    private final List<Point2f> vertices;
    private final Point2i point;
    private Point2f v1;
    private Point2f v2;
    private Point2f v3;
    private Point2f v4;
    private float v1x;
    private float v1y;
    private float v2x;
    private float v2y;
    private float v3x;
    private float v3y;
    private float v4x;
    private float x1;
    private float x2;
    private float dx1;
    private float dx2;
    private float x0;
    private float y0;
    private float limY;
    private int x;
    private int y;
    private ZBuffer zBuffer;
    //private float zPixel;
    private float z1, z2, z3;


    public DDATriangler(GraphicsContext ctx, ZBuffer zBuffer, float z1, float z2, float z3) {
        this.filler = new SolidFiller(HTMLColorf.BLACK);
        this.barycentricser = new Barycentricser();
        this.barycentrics = this.barycentricser.barycentrics();
        this.vertices = new ArrayList(3);
        this.point = new Vector2i(0, 0);
        this.v4 = new Vector2f(0.0F, 0.0F);
        this.writer = ((GraphicsContext) Objects.requireNonNull(ctx)).getPixelWriter();
        this.zBuffer = zBuffer;
        this.z1 = z1;
        this.z2 = z2;
        this.z3 = z3;
    }

    public Filler filler() {
        return this.filler;
    }

    public void setFiller(Filler f) {
        this.filler = (Filler) Objects.requireNonNull(f);
    }


    private void update(Triangle t) {
        this.vertices.clear();
        this.vertices.add(t.v1());
        this.vertices.add(t.v2());
        this.vertices.add(t.v3());
        this.vertices.sort(Comparator.comparing(Point2f::y).thenComparing(Point2f::x));
        this.v1 = (Point2f) this.vertices.get(0);
        this.v2 = (Point2f) this.vertices.get(1);
        this.v3 = (Point2f) this.vertices.get(2);
        this.v1x = this.v1.x();
        this.v1y = this.v1.y();
        this.v2x = this.v2.x();
        this.v2y = this.v2.y();
        this.v3x = this.v3.x();
        this.v3y = this.v3.y();
    }

    private void drawFlat(Point2f p0, Point2f p1, Point2f p2) {
        this.x0 = p0.x();
        this.y0 = p0.y();
        this.dx1 = (p1.x() - this.x0) / (p1.y() - this.y0);
        this.dx2 = (p2.x() - this.x0) / (p2.y() - this.y0);
        this.limY = p1.y();
        if (Floats.moreThan(this.y0, this.limY)) {
            this.drawFlatAtMinY();
        } else {
            this.drawFlatAtMaxY();
        }

    }

    private void drawFlatAtMaxY() {
        this.x1 = this.x0;
        this.x2 = this.x1;

        for (this.y = (int) this.y0; (float) this.y <= this.limY; ++this.y) {
            this.drawHLine();
            this.x1 += this.dx1;
            this.x2 += this.dx2;
        }

    }

    private void drawFlatAtMinY() {
        this.x1 = this.x0;
        this.x2 = this.x1;

        for (this.y = (int) this.y0; (float) this.y > this.limY; --this.y) {
            this.drawHLine();
            this.x1 -= this.dx1;
            this.x2 -= this.dx2;
        }

    }

    private void drawHLine() {
        this.point.setY(this.y);
        for (this.x = (int) this.x1; (float) this.x <= this.x2; ++this.x) {
            this.point.setX(this.x);
            this.barycentricser.calculate((float) this.point.x(), (float) this.point.y());
            float zPixel = barycentricser.barycentrics().lambda1()*z1 +
                    barycentricser.barycentrics().lambda2()*z2 +
                    barycentricser.barycentrics().lambda3()*z3;
            if (this.barycentrics.normalized() && this.barycentrics.inside()) {
                this.color = this.filler.color(this.barycentrics, this.point);
                if (this.color != null) {
                    if (zBuffer.shouldDraw(this.x, this.y, zPixel)) {
                        this.writer.setColor(this.x, this.y, this.color.jfxColor());
                    }

                }
            }
        }

    }

    public void draw(Triangle t) {
        Objects.requireNonNull(t);
        this.barycentricser.setTriangle(t);
        this.update(t);
        if (Floats.equals(this.v2y, this.v3y)) {
            this.drawFlat(this.v1, this.v2, this.v3);
        } else if (Floats.equals(this.v1y, this.v2y)) {
            this.drawFlat(this.v3, this.v1, this.v2);
        } else {
            this.v4x = this.v1x + (this.v2y - this.v1y) / (this.v3y - this.v1y) * (this.v3x - this.v1x);
            this.v4.setX(this.v4x);
            this.v4.setY(this.v2y);
            if (Floats.moreThan(this.v4x, this.v2x)) {
                this.drawFlat(this.v1, this.v2, this.v4);
                this.drawFlat(this.v3, this.v2, this.v4);
            } else {
                this.drawFlat(this.v1, this.v4, this.v2);
                this.drawFlat(this.v3, this.v4, this.v2);
            }

        }
    }
}
