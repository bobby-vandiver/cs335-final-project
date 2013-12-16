package edu.uky.cs335final.basketball.render;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import edu.uky.cs335final.basketball.model.BasketBall;
import edu.uky.cs335final.basketball.camera.Camera;
import edu.uky.cs335final.basketball.R;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.matrix.MatrixUtils;
import edu.uky.cs335final.basketball.shader.OpenGLProgram;
import edu.uky.cs335final.basketball.shader.ShaderUtils;
import edu.uky.cs335final.basketball.shader.TextureUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

public class BasketBallRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = BasketBallRenderer.class.getCanonicalName();

    // The basketball's initial position is relative to the camera's position
    private static final Vector DISPLACEMENT = new Vector(0f, -2f, -2.5f);
    private static final float RADIUS = 1.0f;

    private final float[] viewMatrix = MatrixUtils.newMatrix();
    private final float[] projectionMatrix = MatrixUtils.newMatrix();

    private final Context context;
    private final Vector lightPosition;

    private Camera camera;
    private BasketBall basketBall;

    private List<Renderable> models;

    private boolean shotInProgress;
    private boolean shotFinished;

    private boolean replayInProgress;
    private boolean gameOver;

    public BasketBallRenderer(Context context, Camera camera) {
        Log.d(TAG, "Instantiating renderer");

        this.context = context;
        this.lightPosition = Vector.ORIGIN;

        this.models = new ArrayList<Renderable>();
        this.camera = camera;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "Creating surface");

        glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        createBasketBall();
    }

    private void createBasketBall() {
        Log.d(TAG, "Creating basketball");

        String vertexShaderCode = ShaderUtils.readShaderFromFile(context, R.raw.texture_vertex_shader);
        String fragmentShaderCode = ShaderUtils.readShaderFromFile(context, R.raw.texture_fragment_shader);

        OpenGLProgram program = new OpenGLProgram(vertexShaderCode, fragmentShaderCode);

        if(models.isEmpty()) {
            Vector eye = camera.getPosition();
            Vector position = new Vector(eye).add(DISPLACEMENT);

            int texture = TextureUtils.loadTexture(context, R.drawable.basketball_texture);
            basketBall = new BasketBall(position, RADIUS, program, texture);

            models.add(basketBall);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "Surface changed");
        glViewport(0, 0, width, height);

        final float aspectRatio = (float) width / height;
        frustumM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1, 1, 100);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.v(TAG, "Drawing frame");
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Log.v(TAG, "Creating view matrix");
        camera.createViewMatrix(viewMatrix);

        Log.v(TAG, "Rendering models");
        for(Renderable model : models) {
            model.render(viewMatrix, projectionMatrix, lightPosition);
        }

        if(shotInProgress)
            update();
    }

    private void update() {
        Log.d(TAG, "Updating ball");
        basketBall.update();

        // TODO: Check for collision with the floor (XZ plane)
        if(collidesWithFloor()) {
            Log.d(TAG, "Ball hit the floor");
            setShotCompleteFlags();
            checkForGameOver();
        }
    }

    private void setShotCompleteFlags() {
        shotInProgress = false;
        shotFinished = true;
    }

    private void checkForGameOver() {
        Log.d(TAG, "Checking for game over conditions");

        // TODO: Enable replay button instead of going straight to replay
        if(replayInProgress)
            setGameCompleteFlags();
        else
            startReplay();
    }

    private void setGameCompleteFlags() {
        Log.d(TAG, "Game complete");
        replayInProgress = false;
        gameOver = true;
    }

    private void startReplay() {
        Log.d(TAG, "Preparing for replay shot");
        setReplayFlags();
        basketBall.resetTime();
    }

    private void setReplayFlags() {
        shotFinished = false;
        shotInProgress = true;
        replayInProgress = true;
        gameOver = false;
    }

    private boolean collidesWithFloor() {
        // TODO: Pass plane information into collides()
        return basketBall.collides();
    }

    public boolean canShoot() {
        return !shotFinished && !shotInProgress && !replayInProgress && !gameOver;
    }

    public void shootBall(float power) {
        shotInProgress = true;

        Vector initialVelocity = new Vector(camera.getDirection()).multiply(power);
        basketBall.setInitialVelocity(initialVelocity);
    }
}
