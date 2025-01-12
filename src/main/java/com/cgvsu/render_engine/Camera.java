package com.cgvsu.render_engine;
import io.github.alphameo.linear_algebra.vec.*;
import io.github.alphameo.linear_algebra.mat.*;

public class Camera {

    public Camera(
            final Vector2 rotation,
            final float distance,
            final Vector3 target,
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        this.rotation = rotation;
        this.target = target;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
    }

    public void setRotation(final Vector3 position) {
        this.rotation = rotation;
    }

    public void setTarget(final Vector3 target) {
        this.target = target;
    }

    public void setAspectRatio(final float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Vector2 getRotation() {
        return rotation;
    }

    public Vector3 getTarget() {
        return target;
    }
    public Vector3 getPosition() {
        Vector3 position = new Vec3(target);
        Vec3Math.add(position, new Vec3(
                (float)(distance * Math.sin(rotation.x()) * Math.sin(rotation.y())),
                (float)(distance * Math.cos(rotation.y())),
                (float)(distance * Math.cos(rotation.x()) * Math.sin(rotation.y())))
        );
        return position;
    }

    public void moveRotation(final Vector2 translation) {
        Vec2Math.add(this.rotation, (translation));
    }

    public void moveDistance(final float translation) {
        this.distance += translation;
    }

//    public void movePosition(final Vector3 translation) {
//        //this.position.add(translation);
//        Vec3Math.add(this.position, translation);
//    }

    public void moveTarget(final Vector3 translation) {
        //this.target.add(target);
        Vec3Math.add(this.target, translation);
    }

    Matrix4 getViewMatrix() {
        float phi = rotation.x();
        float theta = rotation.y();

        float upPhi = phi + (float)Math.PI;
        float upTheta = -theta - (float)(Math.PI/2);
        Vector3 up = new Vec3(
                (float)(Math.sin(upPhi) * Math.sin(upTheta)),
                (float)(Math.cos(upTheta)),
                (float)(Math.cos(upPhi) * Math.sin(upTheta))
        );
        return GraphicConveyor.lookAt(getPosition(), target, up);
    }

    Matrix4 getProjectionMatrix() {
        return GraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
    }

    private Vector2 rotation;
    float distance;
    private Vector3 target;
    private float fov;
    private float aspectRatio;
    private float nearPlane;
    private float farPlane;
}