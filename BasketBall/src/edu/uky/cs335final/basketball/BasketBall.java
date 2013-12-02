package edu.uky.cs335final.basketball;

import android.util.Log;
import edu.uky.cs335final.basketball.geometry.Point;
import edu.uky.cs335final.basketball.render.Renderable;
import edu.uky.cs335final.basketball.util.BufferUtils;
import edu.uky.cs335final.basketball.shader.OpenGLProgram;
import edu.uky.cs335final.basketball.shader.ShaderConstants;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static edu.uky.cs335final.basketball.util.MatrixUtils.*;

import static edu.uky.cs335final.basketball.geometry.Point.COMPONENT_SIZE;
import static edu.uky.cs335final.basketball.geometry.Point.COMPONENTS_PER_POINT;

public class BasketBall implements Renderable {

    private static final String TAG = BasketBall.class.getCanonicalName();

    private final OpenGLProgram openGLProgram;

    private FloatBuffer vertexBuffer;

    private final int vertexCount = VERTICAL_SLICES * HORIZONTAL_SLICES * VERTICES_PER_SQUARE;
    private final int vertexStride = COMPONENTS_PER_POINT * COMPONENT_SIZE;

    private static final int VERTICES_PER_SQUARE = 6;

    private static final int HORIZONTAL_SLICES = 20;
    private static final int VERTICAL_SLICES = 40;

    private final float [] vertices;

    private final Point scaleFactor = new Point(2.5f, 2.5f, 2.5f);

    private final boolean wireFrame = true;

    private Point position;

    public BasketBall(Point position, OpenGLProgram program) {
        this.position = position;
        this.openGLProgram = program;

        final int vertexFloatCount = vertexCount * COMPONENTS_PER_POINT;

        Log.d(TAG, "Creating vertex arrays");
        this.vertices = new float[vertexFloatCount];
        createModel(HORIZONTAL_SLICES, VERTICAL_SLICES);

        Log.d(TAG, "Creating vertex buffer");
        this.vertexBuffer = BufferUtils.createBuffer(vertices);
    }

    // TODO: Clean this mess up
    private void createModel(int lats, int longs) {
        int triIndex = 0;
        for(int i = 0; i < lats; i++) {
            double lat0 = Math.PI * (-0.5 + (double) (i) / lats);
            double z0  = Math.sin(lat0);
            double zr0 =  Math.cos(lat0);

            double lat1 = Math.PI * (-0.5 + (double) (i+1) / lats);
            double z1 = Math.sin(lat1);
            double zr1 = Math.cos(lat1);


            //glBegin(GL_QUAD_STRIP);
            for(int j = 0; j < longs; j++) {
                double lng = 2 * Math.PI * (double) (j - 1) / longs;
                double x = Math.cos(lng);
                double y = Math.sin(lng);

                lng = 2 * Math.PI * (double) (j) / longs;
                double x1 = Math.cos(lng);
                double y1 = Math.sin(lng);

                // the first triangle
                vertices[triIndex*9 ] = (float)(x * zr0);    vertices[triIndex*9 + 1 ] = (float)(y * zr0);   vertices[triIndex*9 + 2 ] = (float) z0;
                vertices[triIndex*9 + 3 ] = (float)(x * zr1);    vertices[triIndex*9 + 4 ] = (float)(y * zr1);   vertices[triIndex*9 + 5 ] = (float) z1;
                vertices[triIndex*9 + 6 ] = (float)(x1 * zr0);   vertices[triIndex*9 + 7 ] = (float)(y1 * zr0);  vertices[triIndex*9 + 8 ] = (float) z0;

                triIndex ++;
                vertices[triIndex*9] = (float)(x1 * zr0);   vertices[triIndex*9 + 1 ] = (float)(y1 * zr0);  	vertices[triIndex*9 + 2 ] = (float) z0;
                vertices[triIndex*9 + 3 ] = (float)(x * zr1);    vertices[triIndex*9 + 4 ] = (float)(y * zr1);   	vertices[triIndex*9 + 5 ] = (float) z1;
                vertices[triIndex*9 + 6 ] = (float)(x1 * zr1);    vertices[triIndex*9 + 7 ] = (float)(y1 * zr1); 	vertices[triIndex*9 + 8 ] = (float) z1;

                // in this case, the normal is the same as the vertex, plus the normalization;
//                for (int kk = -9; kk<9 ; kk++) normals[triIndex*9 + kk] = vertices[triIndex*9+kk];
                triIndex ++;
            }
        }
    }

    public void render(float[] viewMatrix, float[] projectionMatrix) {
        final float[] modelViewProjectionMatrix = multiply(projectionMatrix, viewMatrix);

        final int program = openGLProgram.getProgram();
        glUseProgram(program);

        Log.v(TAG, "Binding position");
        final int positionHandle = glGetAttribLocation(program, ShaderConstants.POSITION);
        glEnableVertexAttribArray(positionHandle);
        glVertexAttribPointer(positionHandle, COMPONENTS_PER_POINT, GL_FLOAT, false, vertexStride, vertexBuffer);

        final int mvpMatrixHandle = glGetUniformLocation(program, ShaderConstants.MODEL_VIEW_PROJECTION);

        final Point center = position;

        Log.v(TAG, "Calculating translation matrix");
        final float[] translation = translate(center.x, center.y, center.z);

        Log.v(TAG, "Calculating scale matrix");
        final float[] scale = scale(scaleFactor.x, scaleFactor.y, scaleFactor.z);

        Log.v(TAG, "Combining translation and scale");
        final float[] scaleAndTranslateMatrix = multiply(translation, scale);

        Log.v(TAG, "Combining translation & scale with model view projection");
        final float[] scaleModelViewProjectionMatrix = multiply(modelViewProjectionMatrix, scaleAndTranslateMatrix);

        Log.v(TAG, "Binding to model view projection handle");
        glUniformMatrix4fv(mvpMatrixHandle, 1, false, scaleModelViewProjectionMatrix, 0);

        Log.v(TAG, "Draw arrays");
        final int drawMode = wireFrame ? GL_LINES : GL_TRIANGLES;
        glDrawArrays(drawMode, 0, vertexCount);

        glDisableVertexAttribArray(positionHandle);
    }
}
