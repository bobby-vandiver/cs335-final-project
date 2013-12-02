package edu.uky.cs335final.basketball.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.opengl.GLES20.*;

/*
    The utility methods provided by this class are slightly
     modified versions of the classes developed by Kevin Brothalar
    in "OpenGL ES 2 for Android":

    http://pragprog.com/book/kbogla/opengl-es-2-for-android

    Note: Shaders are stored under res/raw
 */

public class ShaderUtils {

    private static final String TAG = ShaderUtils.class.getCanonicalName();

    public static String readShaderFromFile(Context context, int resourceId) {
        StringBuilder body = new StringBuilder();

        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String nextLine;

            while((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Could not open resource: " + resourceId, e);
        }
        catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("Resource not found: " + resourceId, nfe);
        }

        return body.toString();
    }

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        final int shaderObjectId = glCreateShader(type);

        if(shaderObjectId == 0) {
            Log.w(TAG, "Could not create new shader.");
            return 0;
        }

        glShaderSource(shaderObjectId, shaderCode);
        glCompileShader(shaderObjectId);

        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

        Log.d(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n:" + glGetShaderInfoLog(shaderObjectId));

        if(compileStatus[0] == 0) {
            Log.e(TAG, "Compilation of shader [" + shaderObjectId + "] failed");
            Log.v(TAG, "Deleting shader [" + shaderObjectId + "]");
            glDeleteShader(shaderObjectId);
            return 0;
        }

        return shaderObjectId;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programObjectId = glCreateProgram();

        if(programObjectId == 0) {
            Log.w(TAG, "Could not create new program");
            return 0;
        }

        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId, fragmentShaderId);

        glLinkProgram(programObjectId);

        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

        Log.d(TAG, "Results of linking program:\n" + glGetProgramInfoLog(programObjectId));

        if(linkStatus[0] == 0) {
            Log.e(TAG, "Linking of program [" + programObjectId + "] failed");
            Log.v(TAG, "Deleting shader [" + programObjectId + "]");
            glDeleteProgram(programObjectId);
            return  0;
        }

        return programObjectId;
    }

    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);

        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);

        Log.v(TAG, "Results of validating program: " + validateStatus[0] + "\nLog:" + glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;
    }
}
