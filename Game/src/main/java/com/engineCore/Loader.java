package com.engineCore;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;


public class Loader {
    private List<Integer> vaos = new ArrayList<Integer>();
    private List<Integer> vbos = new ArrayList<Integer>();
    private List<Integer> textures = new ArrayList<Integer>();

    public RawModel loadToVAO(float[] positions, int[] indices, float[] texBuf, float[] shaderBuf) {
        int vaoID = createVAO();
        indicesLists(indices);
        bufferLists(0,3,positions);
        bufferLists(1,2,texBuf);
        shaderIngLists(2,1,shaderBuf);
        unbindVAO();
        return new RawModel(vaoID,indices.length);
    }

    public int loadTextures(String FileName) {
        int width, height;
        ByteBuffer buf;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);
            buf = STBImage.stbi_load(FileName, w, h, c, 4);

            if (buf == null) {
                System.out.println("engine load texture error: " + FileName + "- not loaded" + STBImage.stbi_failure_reason());
                System.err.println("try again");
                System.exit(-1);
            }
            width = w.get();
            height = h.get();
        }
        int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buf);
        return id;
    }

    public void cleanUP() {
        for (int vao:vaos) {
            GL30.glDeleteVertexArrays(vao);
        }

        for (int vbo:vbos) {
            GL15.glDeleteBuffers(vbo);
        }

        for (int tex:textures) {
            GL11.glDeleteTextures(tex);
        }
    }

    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private void bufferLists(int attribNumber, int attribSize, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer fbuf = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fbuf, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribNumber, attribSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void shaderIngLists(int attribNumber, int attribSize, float[] data) {
        int sboID = GL15.glGenBuffers();
        vbos.add(sboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, sboID);
        FloatBuffer fbuf = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fbuf, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribNumber, attribSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void indicesLists(int[] index) {
        int iboID = GL15.glGenBuffers();
        vbos.add(iboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iboID);
        IntBuffer ibuf = storeDataInIntBuffer(index);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, ibuf, GL15.GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIntBuffer(int[] index) {
        IntBuffer buf = MemoryUtil.memAllocInt(index.length);
        buf.put(index);
        buf.flip();
        return buf;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buf = MemoryUtil.memAllocFloat(data.length);
        buf.put(data);
        buf.flip();
        return buf;
    }
}
