package edu.uky.cs335final.basketball.render;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import edu.uky.cs335final.basketball.BasketBall;
import edu.uky.cs335final.basketball.Camera;
import edu.uky.cs335final.basketball.R;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.matrix.MatrixUtils;
import edu.uky.cs335final.basketball.shader.OpenGLProgram;
import edu.uky.cs335final.basketball.shader.ShaderUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

public class BasketBallRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = BasketBallRenderer.class.getCanonicalName();

    private final float[] viewMatrix = MatrixUtils.newMatrix();
    private final float[] projectionMatrix = MatrixUtils.newMatrix();

    private final Context context;

    private Camera camera;
    private List<Renderable> models;

    public BasketBallRenderer(Context context) {
        this.context = context;
        this.models = new ArrayList<Renderable>();

        Vector eye = new Vector(0f, 0f, 12f);
        Vector center = new Vector(0f, 0f, 0f);
        Vector up = new Vector(0f, 1f, 0f);

        this.camera = new Camera(eye, center, up);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        createBasketBall();
    }

    private void createBasketBall() {
        Log.d(TAG, "Creating basketball");

        String vertexShaderCode = ShaderUtils.readShaderFromFile(context, R.raw.vertex_shader);
        String fragmentShaderCode = ShaderUtils.readShaderFromFile(context, R.raw.fragment_shader);

        OpenGLProgram program = new OpenGLProgram(vertexShaderCode, fragmentShaderCode);
        BasketBall basketBall = new BasketBall(Vector.ORIGIN, 2.5f, program);

        models.add(basketBall);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        final float aspectRatio = (float) width / height;
        frustumM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1, 1, 20);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.v(TAG, "Drawing frame");
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Log.v(TAG, "Creating view matrix");
        camera.createViewMatrix(viewMatrix);

        Log.v(TAG, "Rendering models");
        for(Renderable model : models) {
            model.render(viewMatrix, projectionMatrix);
        }
    }
}
