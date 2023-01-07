package com.engineUtil;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

import static org.lwjgl.glfw.GLFW.*;

public class Camera extends Entity {
    private Vector3f position, rotation;
    private float fov;

    public Camera(float fov) {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        this.fov = fov;
    }

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public void movePosition(float x, float y, float z) {
        if (z != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * z;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * z;
        }

        if (x != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90.0f)) * -1.0f * x;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90.0f)) * x;
        }
        position.y += y;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public void moveRotation(float x, float y, float z) {
        this.rotation.x += x;
        this.rotation.y += y;
        this.rotation.z += z;
        System.out.println("x: " + x + " y: " + y);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Matrix4f getViewMatrix() {
        Vector3f pos = getPosition();
        Vector3f rot = getRotation();
        Matrix4f mat = new Matrix4f();
        mat.identity();
        mat.rotate((float) Math.toRadians(rot.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rot.y), new Vector3f(0, 1, 0))
                .rotate((float) Math.toRadians(rot.z), new Vector3f(0, 0, 1));
        mat.translate(-pos.x, -pos.y, -pos.z);

        return mat;
    }

    public Matrix4f getTransMatrix(Entity e) {
        Matrix4f mat = new Matrix4f();
        mat.identity().translate(e.getPosition()).
            rotateX((float) Math.toRadians(e.getRotation().x)).
            rotateY((float) Math.toRadians(e.getRotation().y)).
            rotateZ((float) Math.toRadians(e.getRotation().z)).
            scale(e.getScale());
        return mat;
    }

    public Matrix4f getProjectionMatrix() {
        Matrix4f mat = new Matrix4f();
        mat.identity().perspective((float) Math.toRadians(fov), (float) (1200.0f / 800.0f), 0.1f, 100.0f);
        return mat;
    }
}
