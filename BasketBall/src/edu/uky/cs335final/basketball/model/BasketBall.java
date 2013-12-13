package edu.uky.cs335final.basketball.model;

import android.util.Log;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.matrix.MatrixBuilder;
import edu.uky.cs335final.basketball.render.Renderable;
import edu.uky.cs335final.basketball.util.BufferUtils;
import edu.uky.cs335final.basketball.shader.OpenGLProgram;
import edu.uky.cs335final.basketball.shader.ShaderConstants;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;

import static edu.uky.cs335final.basketball.geometry.Vector.COMPONENT_SIZE;
import static edu.uky.cs335final.basketball.geometry.Vector.COMPONENTS_PER_POINT;

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

    private final Vector scaleFactor;
    private final boolean wireFrame = true;

    private Vector position;
    private Vector initialPosition;

    private static final Vector GRAVITY = new Vector(0.0f, -9.81f, 0.0f);
    private Vector initialVelocity;

    private static final float TIME_DELTA = 0.01f;
    private float time;

    public BasketBall(Vector position, float radius, OpenGLProgram program) {
        this.position = position;
        this.scaleFactor = new Vector(radius, radius, radius);
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

    // Invoking this method indicates an intention to move the basketball in the world
    public void setInitialVelocity(Vector initialVelocity) {
        this.initialVelocity = initialVelocity;
        this.initialPosition = position;
        this.time = 0f;
    }

    // Used to prepare for replay using previously set initialPosition and initialVelocity
    public void resetTime() {
        time = 0f;
    }

    public void update() {
        time += TIME_DELTA;

        Vector velocity = new Vector(initialVelocity).multiply(time);
        Vector acceleration = new Vector(GRAVITY).multiply(time * time).multiply(0.5f);

        position = new Vector(initialPosition).add(velocity).add(acceleration);
    }

    public boolean collides() {
        // TODO: Remove hardcoded checks for XZ plane collision
        return (position.x < 0f) || (position.y < 0f);
    }

    public void render(float[] viewMatrix, float[] projectionMatrix) {
        openGLProgram.useProgram();

        Log.v(TAG, "Binding position");
        final int positionHandle = openGLProgram.bindVertexAttribute(ShaderConstants.POSITION, vertexStride, vertexBuffer);

        final float[] modelViewProjectionMatrix = new MatrixBuilder()
                .scale(scaleFactor.x, scaleFactor.y, scaleFactor.z)
                .translate(position.x, position.y, position.z)
                .multiply(viewMatrix)
                .multiply(projectionMatrix)
                .build();

        openGLProgram.bindUniformMatrix(ShaderConstants.MODEL_VIEW_PROJECTION, modelViewProjectionMatrix);

        Log.v(TAG, "Draw arrays");
        final int drawMode = wireFrame ? GL_LINES : GL_TRIANGLES;
        glDrawArrays(drawMode, 0, vertexCount);

        glDisableVertexAttribArray(positionHandle);
    }
}
