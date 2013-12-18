package edu.uky.cs335final.basketball.shader;

import android.content.Context;
import static edu.uky.cs335final.basketball.shader.ShaderUtils.*;

public class OpenGLProgramFactory {

    public static OpenGLProgram create(Context context, int vertexShaderId, int fragmentShaderId) {

        String vertexShaderCode = readShaderFromFile(context, vertexShaderId);
        String fragmentShaderCode = readShaderFromFile(context, fragmentShaderId);

        return  new OpenGLProgram(vertexShaderCode, fragmentShaderCode);
    }
}
