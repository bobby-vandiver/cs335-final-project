package edu.uky.cs335final.basketball.model.goal;

import android.util.Log;
import edu.uky.cs335final.basketball.collision.BoundingBox;
import edu.uky.cs335final.basketball.geometry.Cuboid;
import edu.uky.cs335final.basketball.geometry.Plane;
import edu.uky.cs335final.basketball.geometry.UnitCuboid;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.matrix.MatrixBuilder;
import edu.uky.cs335final.basketball.render.Renderable;
import edu.uky.cs335final.basketball.shader.OpenGLProgram;
import edu.uky.cs335final.basketball.shader.ShaderConstants;
import edu.uky.cs335final.basketball.util.BufferUtils;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;

import static edu.uky.cs335final.basketball.shader.ShaderConstants.COMPONENTS_PER_TEXTURE_COORDINATE;
import static edu.uky.cs335final.basketball.geometry.Vector.COMPONENT_SIZE;
import static edu.uky.cs335final.basketball.geometry.Vector.COMPONENTS_PER_POINT;


public class Backboard implements Renderable {

    private static final String TAG = Backboard.class.getCanonicalName();

    private static final float[] vertices = UnitCuboid.VERTICES;
    private static final float[] normals = UnitCuboid.NORMALS;

    private static final float[] textureCoordinates = {

            // front
            1.0f, 0.0f,     0.0f, 0.0f,     0.0f, 1.0f,     // v0-v1-v2
            0.0f, 1.0f,     1.0f, 1.0f,     1.0f, 0.0f,     // v2-v3-v0

            // right
            0.1f, 0.1f,     0.1f, 0.1f,     0.1f, 0.1f,
            0.1f, 0.1f,     0.1f, 0.1f,     0.1f, 0.1f,

            // top
            0.1f, 0.1f,     0.1f, 0.1f,     0.1f, 0.1f,
            0.1f, 0.1f,     0.1f, 0.1f,     0.1f, 0.1f,

            // left
            0.1f, 0.1f,     0.1f, 0.1f,     0.1f, 0.1f,
            0.1f, 0.1f,     0.1f, 0.1f,     0.1f, 0.1f,

            // bottom
            0.1f, 0.1f,     0.1f, 0.1f,     0.1f, 0.1f,
            0.1f, 0.1f,     0.1f, 0.1f,     0.1f, 0.1f,

            // back
            0.1f, 0.1f,     0.1f, 0.1f,     0.1f, 0.1f,
            0.1f, 0.1f,     0.1f, 0.1f,     0.1f, 0.1f,
    };

    private final OpenGLProgram openGLProgram;

    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;

    private final int vertexCount = vertices.length / COMPONENTS_PER_POINT;
    private final int vertexStride = COMPONENTS_PER_POINT * COMPONENT_SIZE;

    private FloatBuffer textureCoordinatesBuffer;
    private final int textureStride = COMPONENTS_PER_TEXTURE_COORDINATE * COMPONENT_SIZE;

    private Cuboid board;

    private final int texture;

    public Backboard(Vector position, OpenGLProgram program, int texture) {
        this.openGLProgram = program;
        this.texture = texture;

        Log.d(TAG, "Creating vertex buffer");
        this.vertexBuffer = BufferUtils.createBuffer(vertices);

        Log.d(TAG, "Creating normal buffer");
        this.normalBuffer = BufferUtils.createBuffer(normals);

        Log.d(TAG, "Creating texture coordinates buffer");
        this.textureCoordinatesBuffer = BufferUtils.createBuffer(textureCoordinates);

        this.board = new Cuboid(position, 3f, 2.5f, 1f);
    }

//    public Plane getCollisionPlane() {
//        Vector normal = board.getNormal();
//        Vector point = board.getCenter();
//        return new Plane(normal, point);
//    }

    public BoundingBox getBoundingBox() {

        Vector center = board.getCenter();

        float width = board.getWidth();
        float length = board.getLength();
        float depth = board.getDepth();

        return new BoundingBox(center, width, length, depth);
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

        Log.v(TAG, "Binding texture coordinates");
        openGLProgram.bindVertexAttribute(ShaderConstants.TEXTURE_COORDINATES, COMPONENTS_PER_TEXTURE_COORDINATE, textureStride, textureCoordinatesBuffer);

        Log.v(TAG, "Binding texture");
        openGLProgram.bindTexture2D(ShaderConstants.TEXTURE_UNIT, GL_TEXTURE0, texture);

        final Vector center = board.getCenter();

        final float width = board.getWidth();
        final float length = board.getLength();
        final float depth = board.getDepth();

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
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);

        glDisableVertexAttribArray(positionHandle);
        glDisableVertexAttribArray(normalHandle);
    }
}
