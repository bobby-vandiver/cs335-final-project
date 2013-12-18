package edu.uky.cs335final.basketball.render;

import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_TRIANGLES;

// Provide centralized configuration for toggling wireframe rendering
public class RenderConfig {

    private static boolean WIREFRAME = false;

    public static int getDrawMode() {
        return WIREFRAME ? GL_LINES : GL_TRIANGLES;
    }
}
