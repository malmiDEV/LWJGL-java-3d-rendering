package com.engineUtil;

import org.joml.Vector2dc;
import org.joml.Vector2f;
import org.joml.Vector2d;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import com.engineUtil.Window;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {
    private final Vector2d prevPos, curPos;
    private final Vector2f displayVec;
    private boolean inWindow = false;

    public MouseInput() {
        this.prevPos = new Vector2d(-1, -1);
        this.curPos = new Vector2d(0, 0);
        this.displayVec = new Vector2f();
    }

    public void input(long win) {
        displayVec.x = 0;
        displayVec.y = 0;

        DoubleBuffer xbuf = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer ybuf = BufferUtils.createDoubleBuffer(1);

        GLFW.glfwGetCursorPos(win, xbuf, ybuf);

        xbuf.rewind();
        ybuf.rewind();

        curPos.x = xbuf.get();
        curPos.y = ybuf.get();

        double deltaX = curPos.x - prevPos.x;
        double deltaY = curPos.y - prevPos.y;

        boolean rotX = curPos.x != 0;
        boolean rotY = curPos.y != 0;

        if (rotY) {
            displayVec.y = (float) deltaX;
        }
        if (rotX) {
            displayVec.x = (float) deltaY;
        }

        prevPos.x = curPos.x;
        prevPos.y = curPos.y;
    }

    public Vector2f getDisplayVec() {
        return displayVec;
    }
}
