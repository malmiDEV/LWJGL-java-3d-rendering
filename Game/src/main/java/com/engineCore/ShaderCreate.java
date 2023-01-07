package com.engineCore;


import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ShaderCreate {
    private int vertexID;
    private int fragmentID;
    private int programID;

    private final Map<String, Integer> uniforms;

    public ShaderCreate(String vertPath, String fragPath) {
        this.programID = GL20.glCreateProgram();

        uniforms = new HashMap<>();

        this.vertexID = shader_reader(vertPath, GL20.GL_VERTEX_SHADER);
        this.fragmentID = shader_reader(fragPath, GL20.GL_FRAGMENT_SHADER);

        GL20.glAttachShader(programID, vertexID);
        GL20.glAttachShader(programID, fragmentID);

        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);

        GL20.glDeleteShader(vertexID);
        GL20.glDeleteShader(fragmentID);
    }

    public void createUniforms(String uniformsName) {
        int uniformsLoc = GL20.glGetUniformLocation(programID, uniformsName);
        uniforms.put(uniformsName, uniformsLoc);
    }

    public void shaderEnable() {
        GL20.glUseProgram(programID);
    }

    public void setUniforms(String uniformsName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL20.glUniformMatrix4fv(uniforms.get(uniformsName), false, value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniforms(String uniformsName, int value) {
        GL20.glUniform1i(uniforms.get(uniformsName), value);
    }
    public void shaderDisable() {
        GL20.glUseProgram(0);
    }

    public void shaderDelete() {
        GL20.glDetachShader(programID, vertexID);
        GL20.glDetachShader(programID, fragmentID);
        GL20.glDeleteProgram(programID);
    }

    private int shader_reader(String file, int type) {
        StringBuilder shader_src = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                shader_src.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("file cant read");
            e.printStackTrace();
            System.exit(-1);
        }

        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shader_src);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("shader cant compile");
            System.exit(-1);
        }

        return shaderID;
    }

}
