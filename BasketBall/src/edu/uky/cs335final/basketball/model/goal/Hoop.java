package edu.uky.cs335final.basketball.model.goal;

import android.util.Log;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.matrix.MatrixBuilder;
import edu.uky.cs335final.basketball.render.Renderable;
import edu.uky.cs335final.basketball.shader.OpenGLProgram;
import edu.uky.cs335final.basketball.shader.ShaderConstants;
import edu.uky.cs335final.basketball.util.BufferUtils;
import edu.uky.cs335final.basketball.util.ColorUtils;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static android.util.FloatMath.*;

import static edu.uky.cs335final.basketball.geometry.Vector.COMPONENTS_PER_POINT;
import static edu.uky.cs335final.basketball.geometry.Vector.COMPONENT_SIZE;

public class Hoop implements Renderable {

    private static final String TAG = Hoop.class.getCanonicalName();

    private final OpenGLProgram openGLProgram;

    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;

    private final int vertexCount = VERTICAL_SLICES * HORIZONTAL_SLICES * VERTICES_PER_SQUARE;
    private final int vertexStride = COMPONENTS_PER_POINT * COMPONENT_SIZE;

    private static final int VERTICES_PER_SQUARE = 6;

    private static final float HOOP_RADIUS = 1.2f;
    private static final float RIM_RADIUS = 0.25f;

    private static final int HORIZONTAL_SLICES = 20;
    private static final int VERTICAL_SLICES = 40;

    private final float[] vertices;
    private final float[] normals;

    private Vector position;

    private final float[] color = ColorUtils.fromHexCode("#ff8800");

    private final float[] diffuseColor = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    private final float diffuseIntensity = 1f;

    final float[] specularColor = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    final float specularIntensity = 1f;
    final float shininess = 100.0f;

    public Hoop(Vector position, OpenGLProgram program) {
        this.position = position;
        this.openGLProgram = program;

        final int vertexFloatCount = vertexCount * COMPONENTS_PER_POINT;

        Log.d(TAG, "Creating vertex arrays");
        this.vertices = new float[vertexFloatCount];
        this.normals = new float[vertexFloatCount];

        createModel(HOOP_RADIUS, RIM_RADIUS, HORIZONTAL_SLICES, VERTICAL_SLICES);

        Log.d(TAG, "Creating vertex buffer");
        this.vertexBuffer = BufferUtils.createBuffer(vertices);

        Log.d(TAG, "Creating normal buffer");
        this.normalBuffer = BufferUtils.createBuffer(normals);
    }

    // Adaptation of algorithm found here;
    // https://github.com/htbegin/pyftk/blob/master/opengles/redbook/torus.c
    private void createModel(float hoopRadius, float rimRadius, int stacks, int slices) {

        final float TWO_PI = (float) Math.PI * 2.0f;
        int idx = 0;

        for(int i = 0; i < stacks; i++) {
            for(int j = 0; j <= slices; j++) {
                for(int k = 1; k >= 0; k--) {

                    float s = (float) (i + k) % stacks + 0.5f;
                    float t = (float) j % slices;

                    float phi = s * TWO_PI / stacks;
                    float theta = t * TWO_PI / slices;

                    float cosPhi = cos(phi);
                    float cosTheta = cos(theta);

                    float sinPhi = sin(phi);
                    float sinTheta = sin(theta);

                    float x = (hoopRadius + rimRadius * cosPhi) * cosTheta;
                    float y = (hoopRadius + rimRadius * cosPhi) * sinTheta;
                    float z = rimRadius * sinPhi;

                    vertices[idx++] = x;
                    vertices[idx++] = y;
                    vertices[idx++] = z;
                }
            }
        }

        System.arraycopy(vertices, 0, normals, 0, vertices.length);
    }

    @Override
    public void render(float[] viewMatrix, float[] projectionMatrix, Vector lightPosition) {
        openGLProgram.useProgram();

        Log.v(TAG, "Binding light position");
        openGLProgram.bindUniformVector3(ShaderConstants.LIGHT_POSITION, lightPosition.asVec3());

        Log.v(TAG, "Binding normals");
        final int normalHandle = openGLProgram.bindVertexAttribute(ShaderConstants.NORMAL, COMPONENTS_PER_POINT, vertexStride, normalBuffer);

        Log.v(TAG, "Building normal transformation matrix");

        final float[] normalTransformationMatrix = new MatrixBuilder()
                .multiply(viewMatrix)
                .invert()
                .transpose()
                .build();

        Log.v(TAG, "Binding normal transformation");
        openGLProgram.bindUniformMatrix(ShaderConstants.NORMAL_TRANSFORMATION, normalTransformationMatrix);

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

        Log.v(TAG, "Building model view matrix");

        final float[] modelViewMatrix = new MatrixBuilder()
                .rotate(90, 1, 0, 0)
                .translate(position.x, position.y, position.z)
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
        glDrawArrays(GL_TRIANGLE_STRIP, 0, vertexCount);

        glDisableVertexAttribArray(positionHandle);
        glDisableVertexAttribArray(normalHandle);
    }
}
