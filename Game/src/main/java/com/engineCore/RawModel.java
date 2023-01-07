package com.engineCore;

public class RawModel {
    private int vaoID;
    private int vertexCount;
    private Texture texture;

    public RawModel(int vaoID, int vertexCount) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
    }

    public RawModel(RawModel model, Texture texture) {
        this.vaoID = model.getVaoID();
        this.vertexCount = model.getVertexCount();
        this.texture = texture;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getVaoID() {
        return vaoID;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
