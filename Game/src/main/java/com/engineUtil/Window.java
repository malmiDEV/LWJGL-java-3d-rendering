package com.engineUtil;

import com.engineCore.*;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.*;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static java.sql.Types.NULL;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

public class Window {
    private Vector2i winSize;
    private String name;
    private long handle;

    private static Window window = null;

    private Loader loader;
    private Renderer renderer;
    private ShaderCreate shader;
    private Entity entity;
    private Camera camera;
    private MouseInput msInput;
    private Vector3f cameraInc;
    private static final float CAMERA_SPEED = 5.0f;
    private static final float MOUSE_SENSITIVITY = 0.08f;
    private float lastTime = 0f;
    private float deltaTime = 0f;
    private boolean isWire = false;
    private List<Entity> entities = new ArrayList<Entity>();

    public Window() {
        this.winSize = new Vector2i(1200, 800);
        this.name = "Game";
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }
    public void run() {
        GLFWErrorCallback.createPrint(System.err).set();

        init();
        loop();

        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);

        shader.shaderDelete();
        glfwTerminate();
    }

    public void init() {
        if (!glfwInit()) {
            throw new IllegalStateException("uninitialize engine");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        handle = glfwCreateWindow(this.winSize.x, this.winSize.y, this.name, NULL, NULL);
        if (handle == NULL) {
            throw new IllegalStateException("uninitialize engine window");
        }


        glfwMakeContextCurrent(handle);
        glfwSwapInterval(1);

        glfwShowWindow(handle);

        GL.createCapabilities();

        float[] vertices = new float[] {
                0.5f,  0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f, -0.5f, -0.5f,  0.5f,  0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f,  0.5f, -0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,  0.5f,  0.5f, -0.5f,  -0.5f,  0.5f, -0.5f,  -0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f, -0.5f, -0.5f, -0.5f,  0.5f, -0.5f, -0.5f,  0.5f, -0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f, -0.5f, -0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f,  0.5f,  0.5f,
                0.5f,  0.5f, -0.5f,  0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f,  0.5f, -0.5f,
        };

        float[] tex_coords = new float[]{
                0.0f, 1.0f,  0.0f, 0.0f,  1.0f, 0.0f,  1.0f, 1.0f,
                0.0f, 1.0f,  0.0f, 0.0f,  1.0f, 0.0f,  1.0f, 1.0f,
                0.0f, 1.0f,  0.0f, 0.0f,  1.0f, 0.0f,  1.0f, 1.0f,
                0.0f, 1.0f,  0.0f, 0.0f,  1.0f, 0.0f,  1.0f, 1.0f,
                0.0f, 1.0f,  0.0f, 0.0f,  1.0f, 0.0f,  1.0f, 1.0f,
                0.0f, 1.0f,  0.0f, 0.0f,  1.0f, 0.0f,  1.0f, 1.0f,
        };

        int[] indices = new int[]{
                0,   1,  2,  0,  2,  3,
                4,   5,  6,  4,  6,  7,
                8,   9, 10,  8, 10, 11,
                12, 13, 14, 12, 14, 15,
                16, 17, 18, 16, 18, 19,
                20, 21, 22, 20, 22, 23,
        };

        float[] shaderIng = new float[] {
                0.6f, 0.6f, 0.6f, 0.6f,
                0.6f, 0.6f, 0.6f, 0.6f,
                1.0f, 1.0f, 1.0f, 1.0f,
                0.4f, 0.4f, 0.4f, 0.4f,
                0.8f, 0.8f, 0.8f, 0.8f,
                0.8f, 0.8f, 0.8f, 0.8f,
        };

        loader = new Loader();
        renderer = new Renderer();

        shader = new ShaderCreate("Data/Shaders/vertex.glsl", "Data/Shaders/fragment.glsl");
        shader.createUniforms("textureSampler");
        shader.createUniforms("m");
        shader.createUniforms("v");
        shader.createUniforms("p");

        RawModel model = loader.loadToVAO(vertices, indices, tex_coords, shaderIng);
        model.setTexture(new Texture(loader.loadTextures("Data/Texture/grass.png")));

        GLFW.glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        msInput = new MouseInput();

        camera = new Camera(75.0f);
        cameraInc = new Vector3f(0, 0, 0);
        camera.setPosition(0, 0, 3);
        camera.setRotation(0, -90, 0);

        for (int x = -10; x < 10; x++) {
            for (int z = -10; z < 10; z++) {
                entities.add(new Entity(model, new Vector3f(x, 0, z), new Vector3f(0, 0, 0), 1f));
            }
        }
    }

    public void calcDelta() {
        float curTime = (float) glfwGetTime();
        deltaTime = curTime - lastTime;
        lastTime = curTime;
    }

    public void input() {
        msInput.input(handle);

        cameraInc.set(0, 0, 0);
        if (glfwGetKey(handle, GLFW_KEY_W) == GLFW_PRESS)
            cameraInc.z = -1;

        if (glfwGetKey(handle, GLFW_KEY_S) == GLFW_PRESS)
            cameraInc.z = 1;

        if (glfwGetKey(handle, GLFW_KEY_A) == GLFW_PRESS)
            cameraInc.x = -1;

        if (glfwGetKey(handle, GLFW_KEY_D) == GLFW_PRESS)
            cameraInc.x = 1;

        if (glfwGetKey(handle, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS)
            cameraInc.y = -1;

        if (glfwGetKey(handle, GLFW_KEY_SPACE) == GLFW_PRESS)
            cameraInc.y = 1;

        if (glfwGetKey(handle, GLFW_KEY_T) == GLFW_PRESS)
            isWire = true;
        else
            isWire = false;
    }

    public void update() {
        this.calcDelta();
        camera.movePosition(cameraInc.x * CAMERA_SPEED * deltaTime, cameraInc.y * CAMERA_SPEED * deltaTime, cameraInc.z * CAMERA_SPEED * deltaTime);

        Vector2f rotVec = msInput.getDisplayVec();
        camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

        if (camera.getRotation().x > 89.0f) {
            camera.getRotation().x = 89.0f;
        }if (camera.getRotation().x < -89.0f) {
            camera.getRotation().x = -89.0f;
        }
    }

    public void loop() {
        while (!glfwWindowShouldClose(handle)) {
            glfwPollEvents();

            renderer.prepare();

            shader.shaderEnable();

            shader.setUniforms("textureSampler", 0);

            this.input();
            this.update();

            shader.setUniforms("p", camera.getProjectionMatrix());
            shader.setUniforms("v", camera.getViewMatrix());

            for (Entity e : entities) {
                shader.setUniforms("m", camera.getTransMatrix(e));
                renderer.render(e, isWire);
            }
            shader.shaderDisable();

            glfwSwapBuffers(handle);
        }
    }


    public long geth() {
        return handle;
    }
}
