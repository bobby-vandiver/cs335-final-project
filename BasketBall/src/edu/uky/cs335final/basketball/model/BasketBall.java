package edu.uky.cs335final.basketball.model;

import android.util.Log;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.matrix.MatrixBuilder;
import edu.uky.cs335final.basketball.render.Renderable;
import edu.uky.cs335final.basketball.util.BufferUtils;
import edu.uky.cs335final.basketball.shader.OpenGLProgram;
import edu.uky.cs335final.basketball.shader.ShaderConstants;

import java.nio.FloatBuffer;

import static android.util.FloatMath.*;
import static android.opengl.GLES20.*;

import static edu.uky.cs335final.basketball.geometry.Vector.COMPONENT_SIZE;
import static edu.uky.cs335final.basketball.geometry.Vector.COMPONENTS_PER_POINT;

public class BasketBall implements Renderable {

    private static final String TAG = BasketBall.class.getCanonicalName();

    private final OpenGLProgram openGLProgram;

    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;

    private FloatBuffer textureCoordinatesBuffer;

    private final static int COMPONENTS_PER_TEXTURE_COORDINATE = 2;
    private final int textureStride = COMPONENTS_PER_TEXTURE_COORDINATE * COMPONENT_SIZE;

    private final int vertexCount = VERTICAL_SLICES * HORIZONTAL_SLICES * VERTICES_PER_SQUARE;
    private final int vertexStride = COMPONENTS_PER_POINT * COMPONENT_SIZE;

    private static final int VERTICES_PER_SQUARE = 6;

    private static final int HORIZONTAL_SLICES = 20;
    private static final int VERTICAL_SLICES = 40;

    private final float[] vertices;
    private final float[] normals;

    private final float[] textureCoordinates;

    private final Vector scaleFactor;
    private final boolean wireFrame = false;

    private Vector position;
    private Vector initialPosition;

    private static final Vector GRAVITY = new Vector(0.0f, -9.81f, 0.0f);
    private Vector initialVelocity;

    private static final float TIME_DELTA = 0.01f;
    private float time;

    private final int texture;
    private final int bumpMap;

    public BasketBall(Vector position, float radius, OpenGLProgram program, int texture, int bumpMap) {

        Log.d(TAG, "Constructing basketball");
        Log.d(TAG, "position [" + position + "]");
        Log.d(TAG, "radius [" + radius + "]");

        Log.d(TAG, "texture [" + texture + "]");
        Log.d(TAG, "bumpMap [" + bumpMap + "]");

        this.position = position;
        this.scaleFactor = new Vector(radius, radius, radius);
        this.openGLProgram = program;

        this.texture = texture;
        this.bumpMap = bumpMap;

        final int vertexFloatCount = vertexCount * COMPONENTS_PER_POINT;

        Log.d(TAG, "Creating vertex arrays");
        this.vertices = new float[vertexFloatCount];
        this.normals = new float[vertexFloatCount];

        final int textureCoordinateFloatCount = vertexCount * COMPONENTS_PER_TEXTURE_COORDINATE;
        this.textureCoordinates = new float[textureCoordinateFloatCount];

        createModel(HORIZONTAL_SLICES, VERTICAL_SLICES);

        Log.d(TAG, "Creating vertex buffer");
        this.vertexBuffer = BufferUtils.createBuffer(vertices);

        Log.d(TAG, "Creating texture coordinates buffer");
        this.textureCoordinatesBuffer = BufferUtils.createBuffer(textureCoordinates);
    }

    // TODO: Clean this mess up
    private void createModel(int stacks, int slices) {

        int vertexIndex = 0;

        for(int i = 0; i < stacks; i++) {

            float theta_0 = (float) (Math.PI * (-0.5 + (float) (i) / stacks));
            float sinTheta_0  = sin(theta_0);
            float cosTheta_0 =  cos(theta_0);

            float theta_1 = (float)((Math.PI * (-0.5 + (float) (i+1) / stacks)));
            float sinTheta_1 = sin(theta_1);
            float cosTheta_1 = cos(theta_1);

            for(int j = 0; j < slices; j++) {
                float phi_0 = (float)(2 * Math.PI * (float) (j - 1) / slices);

                float cosPhi_0 = cos(phi_0);
                float sinPhi_0 = sin(phi_0);

                float phi_1 = (float)(2 * Math.PI * (float) (j) / slices);
                float cosPhi_1 = cos(phi_1);
                float sinPhi_1 = sin(phi_1);

                // the first triangle
                int idx = vertexIndex * 9;
                int textureIdx = vertexIndex * 6;

                writeVertex(idx, cosPhi_0 * cosTheta_0, sinPhi_0 * cosTheta_0,  sinTheta_0);
                writeTextureCoordinate(textureIdx, 0, 1);

                writeVertex(idx + 3, cosPhi_0 * cosTheta_1, sinPhi_0 * cosTheta_1,  sinTheta_1);
                writeTextureCoordinate(textureIdx + 2, 0, 0);

                writeVertex(idx + 6, cosPhi_1 * cosTheta_0, sinPhi_1 * cosTheta_0,  sinTheta_0);
                writeTextureCoordinate(textureIdx + 4, 1, 0);

                vertexIndex++;
                textureIdx++;

                // the second triangle
                idx = vertexIndex * 9;
                textureIdx = vertexIndex * 6;

                writeVertex(idx, cosPhi_1 * cosTheta_0, sinPhi_1 * cosTheta_0,  sinTheta_0);
                writeTextureCoordinate(textureIdx, 1, 1);

                writeVertex(idx + 3, cosPhi_0 * cosTheta_1, sinPhi_0 * cosTheta_1,  sinTheta_1);
                writeTextureCoordinate(textureIdx + 2, 0, 1);

                writeVertex(idx + 6, cosPhi_1 * cosTheta_1, sinPhi_1 * cosTheta_1, sinTheta_1);
                writeTextureCoordinate(textureIdx + 4, 1, 0);

                // in this case, the normal is the same as the vertex, plus the normalization;
                for (int k = -9; k < 9 ; k++) {
                    int m = vertexIndex * 9 + k;
                    normals[m] = vertices[m];
                }

                vertexIndex ++;
            }
        }
    }

    private void writeVertex(int position, float x, float y, float z) {
        vertices[position++] = x;
        vertices[position++] = y;
        vertices[position] = z;
    }

    private void writeTextureCoordinate(int position, float s, float t) {
        textureCoordinates[position++] = s;
        textureCoordinates[position] = t;
    }

    // Invoking this method indicates an intention to move the basketball in the world
    public void setInitialVelocity(Vector initialVelocity) {
        Log.d(TAG, "initialVelocity [" + initialVelocity + "]");
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

    public void render(float[] viewMatrix, float[] projectionMatrix, Vector lightPosition) {
        openGLProgram.useProgram();

        Log.v(TAG, "Binding position");
        final int positionHandle = openGLProgram.bindVertexAttribute(ShaderConstants.POSITION, COMPONENTS_PER_POINT, vertexStride, vertexBuffer);

        Log.v(TAG, "Binding texture coordinates");
        openGLProgram.bindVertexAttribute(ShaderConstants.TEXTURE_COORDINATES, COMPONENTS_PER_TEXTURE_COORDINATE, textureStride, textureCoordinatesBuffer);

        Log.v(TAG, "Binding texture");
        openGLProgram.bindTexture2D(ShaderConstants.TEXTURE_UNIT, GL_TEXTURE0, texture);

        Log.v(TAG, "Binding bump map");
        openGLProgram.bindTexture2D(ShaderConstants.BUMP_MAP_UNIT, GL_TEXTURE1, bumpMap);

        final float[] modelViewMatrix = new MatrixBuilder()
                .scale(scaleFactor.x, scaleFactor.y, scaleFactor.z)
                .translate(position.x, position.y, position.z)
                .multiply(viewMatrix)
                .build();

        final float[] modelViewProjectionMatrix = new MatrixBuilder()
                .multiply(modelViewMatrix)
                .multiply(projectionMatrix)
                .build();

        openGLProgram.bindUniformMatrix(ShaderConstants.MODEL_VIEW_PROJECTION, modelViewProjectionMatrix);

        Log.v(TAG, "Draw arrays");
        final int drawMode = wireFrame ? GL_LINES : GL_TRIANGLES;
        glDrawArrays(drawMode, 0, vertexCount);

        glDisableVertexAttribArray(positionHandle);
    }
}
