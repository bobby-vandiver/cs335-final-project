package edu.uky.cs335final.basketball.shader;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.glVertexAttribPointer;

public class OpenGLProgram {

    private final int program;

    public OpenGLProgram(String vertexShaderCode, String fragmentShaderCode) {
        final int vertexShader = ShaderUtils.compileVertexShader(vertexShaderCode);
        final int fragmentShader = ShaderUtils.compileFragmentShader(fragmentShaderCode);
        this.program = ShaderUtils.linkProgram(vertexShader, fragmentShader);
    }

    public void useProgram() {
        glUseProgram(program);
    }

    public int bindVertexAttribute(String location, int componentCount, int vertexStride, FloatBuffer vertexBuffer) {
        final int handle = glGetAttribLocation(program, location);
        glEnableVertexAttribArray(handle);
        glVertexAttribPointer(handle, componentCount, GL_FLOAT, false, vertexStride, vertexBuffer);
        return handle;
    }

    public int bindUniformMatrix(String location, float[] matrix) {
        final int handle = glGetUniformLocation(program, location);
        glUniformMatrix4fv(handle, 1, false, matrix, 0);
        return handle;
    }

    public int bindUniformFloat(String location, float value) {
        final int handle = glGetUniformLocation(program, location);
        glUniform1f(handle, value);
        return handle;
    }

    public int bindUniformVector3(String location, float[] vector) {
        final int handle = glGetUniformLocation(program, location);
        glUniform3fv(handle, 1, vector, 0);
        return handle;
    }

    public int bindUniformVector4(String location, float[] vector) {
        final int handle = glGetUniformLocation(program, location);
        glUniform4fv(handle, 1, vector, 0);
        return handle;
    }

    public int bindTexture2D(String location, final int textureUnitId, final int textureId) {
        final int handle = glGetUniformLocation(program, location);

        glActiveTexture(textureUnitId);
        glBindTexture(GL_TEXTURE_2D, textureId);

        int normalizedUnitId = (textureUnitId - GL_TEXTURE0);
        glUniform1i(handle, normalizedUnitId);

        return handle;
    }
}
