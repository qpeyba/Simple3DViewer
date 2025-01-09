package com.cgvsu.obj_io.writer;

import com.cgvsu.render_engine.Camera;
import io.github.alphameo.linear_algebra.vec.*;
import io.github.alphameo.linear_algebra.vec.Vector3;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CameraTest {
    @Test
    void movePositionTest() {
        Camera camera = new Camera(
                new Vec3(1, 2, 3),
                new Vec3(0, 0, 0),
                1.0F, 1, 0.01F, 100);

        Vector3 translation = new Vec3(-1, 3.5f, 8);
        Vector3 aim = new Vec3(0, 5.5f, 11);
        camera.movePosition(translation);

        Assertions.assertEquals(aim, camera.getPosition());
    }

    @Test
    void moveTargetTest() {
        Camera camera = new Camera(
                new Vec3(1, 2, 3),
                new Vec3(0, 0, 0),
                1.0F, 1, 0.01F, 100);

        Vector3 translation = new Vec3(-1, 3.5f, 8);
        Vector3 aim = new Vec3(-1, 3.5f, 8);
        camera.moveTarget(translation);

        Assertions.assertEquals(aim, camera.getTarget());
    }
}
