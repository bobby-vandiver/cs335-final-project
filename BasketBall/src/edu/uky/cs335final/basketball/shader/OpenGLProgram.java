package edu.uky.cs335final.basketball.shader;

public class OpenGLProgram {

    private final int program;

    public OpenGLProgram(String vertexShaderCode, String fragmentShaderCode) {
        final int vertexShader = ShaderUtils.compileVertexShader(vertexShaderCode);
        final int fragmentShader = ShaderUtils.compileFragmentShader(fragmentShaderCode);
        this.program = ShaderUtils.linkProgram(vertexShader, fragmentShader);
    }

    public int getProgram() {
        return program;
    }
}
