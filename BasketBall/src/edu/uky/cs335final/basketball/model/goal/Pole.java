package edu.uky.cs335final.basketball.model.goal;

import android.util.Log;
import edu.uky.cs335final.basketball.geometry.Cuboid;
import edu.uky.cs335final.basketball.geometry.UnitCuboid;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.matrix.MatrixBuilder;
import edu.uky.cs335final.basketball.render.RenderConfig;
import edu.uky.cs335final.basketball.render.Renderable;
import edu.uky.cs335final.basketball.shader.OpenGLProgram;
import edu.uky.cs335final.basketball.shader.ShaderConstants;
import edu.uky.cs335final.basketball.util.BufferUtils;
import edu.uky.cs335final.basketball.util.ColorUtils;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;

import static edu.uky.cs335final.basketball.geometry.Vector.COMPONENTS_PER_POINT;
import static edu.uky.cs335final.basketball.geometry.Vector.COMPONENT_SIZE;

public class Pole implements Renderable {

    private static final String TAG = Pole.class.getCanonicalName();

    private static final float[] vertices = UnitCuboid.VERTICES;
    private static final float[] normals = UnitCuboid.NORMALS;

    private final OpenGLProgram openGLProgram;

    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;

    private final int vertexCount = vertices.length / COMPONENTS_PER_POINT;
    private final int vertexStride = COMPONENTS_PER_POINT * COMPONENT_SIZE;

    private final float[] color = ColorUtils.fromHexCode("#C0C0C0");
    private Cuboid rectangularPrism;

    private final float[] diffuseColor = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    private final float diffuseIntensity = 0.4f;

    final float[] specularColor = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    final float specularIntensity = 0.7f;
    final float shininess = 100.0f;

    public Pole(Vector position, OpenGLProgram program) {
        this.openGLProgram = program;

        Log.d(TAG, "Creating vertex buffer");
        this.vertexBuffer = BufferUtils.createBuffer(vertices);

        Log.d(TAG, "Creating normal buffer");
        this.normalBuffer = BufferUtils.createBuffer(normals);

        this.rectangularPrism = new Cuboid(position, 0.5f, 5f, 1f);
    }

    @Override
    public void render(float[] viewMatrix, float[] projectionMatrix, Vector lightPosition) {
        openGLProgram.useProgram();

        Log.v(TAG, "Binding light position");
        openGLProgram.bindUniformVector3(ShaderConstants.LIGHT_POSITION, lightPosition.asVec3());

        Log.v(TAG, "Binding normals");
        final int normalHandle = openGLProgram.bindVertexAttribute(ShaderConstants.NORMAL, COMPONENTS_PER_POINT, vertexStride, normalBuffer);

        Log.v(TAG, "Binding vertices");
        final int positionHandle = openGLProgram.bindVertexAttribute(ShaderConstants.POSITION, COMPONENTS_PER_POINT, vertexStride, vertexBuffer);

        Log.v(TAG, "Binding color");
        openGLProgram.bindUniformVector4(ShaderConstants.COLOR, color);

        Log.v(TAG, "Binding diffuse lighting params");
        openGLProgram.bindUniformVector4(ShaderConstants.DIFFUSE_COLOR, diffuseColor);
        openGLProgram.bindUniformFloat(ShaderConstants.DIFFUSE_INTENSITY, diffuseIntensity);

        Log.v(TAG, "Binding specular lighting params");
        openGLProgram.bindUniformVector4(ShaderConstants.SPECULAR_COLOR, specularColor);
        openGLProgram.bindUniformFloat(ShaderConstants.SPECULAR_INTENSITY, specularIntensity);
        openGLProgram.bindUniformFloat(ShaderConstants.SHININESS, shininess);

        final Vector center = rectangularPrism.getCenter();

        final float width = rectangularPrism.getWidth();
        final float length = rectangularPrism.getLength();
        final float depth = rectangularPrism.getDepth();

        Log.v(TAG, "Building model view matrix");

        final float[] modelViewMatrix = new MatrixBuilder()
                .scale(width, length, depth)
                .translate(center.x, center.y, center.z)
                .multiply(viewMatrix)
                .build();

        final float[] modelViewProjectionMatrix = new MatrixBuilder()
                .multiply(modelViewMatrix)
                .multiply(projectionMatrix)
                .build();

        Log.v(TAG, "Binding model view projection");
        openGLProgram.bindUniformMatrix(ShaderConstants.MODEL_VIEW_PROJECTION, modelViewProjectionMatrix);

        Log.v(TAG, "Binding model view");
        openGLProgram.bindUniformMatrix(ShaderConstants.MODEL_VIEW, modelViewMatrix);

        Log.v(TAG, "Draw arrays");
        final int drawMode = RenderConfig.getDrawMode();
        glDrawArrays(drawMode, 0, vertexCount);

        glDisableVertexAttribArray(positionHandle);
        glDisableVertexAttribArray(normalHandle);
    }}
