package edu.uky.cs335final.basketball.render;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import edu.uky.cs335final.basketball.collision.BoundingBox;
import edu.uky.cs335final.basketball.collision.CollisionDetector;
import edu.uky.cs335final.basketball.geometry.Plane;
import edu.uky.cs335final.basketball.geometry.Sphere;
import edu.uky.cs335final.basketball.model.BasketBall;
import edu.uky.cs335final.basketball.camera.Camera;
import edu.uky.cs335final.basketball.R;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.matrix.MatrixUtils;
import edu.uky.cs335final.basketball.model.goal.Goal;
import edu.uky.cs335final.basketball.model.goal.GoalFactory;
import edu.uky.cs335final.basketball.shader.OpenGLProgram;
import edu.uky.cs335final.basketball.shader.OpenGLProgramFactory;
import edu.uky.cs335final.basketball.shader.TextureUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.util.ArrayList;
import java.util.List;

import static edu.uky.cs335final.basketball.collision.CollisionDetector.*;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

public class BasketBallRenderer implements GLSurfaceView.Renderer {

    // Define listener to allow other components to perform specific operations
    // at different points in the lifecycle of the shot
    public static interface ShotListener {

        // This is executed when a shot begins
        public void onStart();

        // This is executed when a shot is first completed regardless of
        // whether it was successful or not. This does NOT get executed at
        // the end of a replay.

        public void onComplete();

        // This is executed when a shot successfully enters the hoop.
        public void onSuccess();

        // This is executed when a shot misses the hoop.
        public void onFailure();
    }

    // Define listener to allow other components to perform specific operations
    // at different points of a replay event
    public static interface ReplayListener {

        // This is executed when a replay is first started
        public void onStart();

        // This is executed when the replay is finished
        public void onStop();
    }

    private static final String TAG = BasketBallRenderer.class.getCanonicalName();

    // The basketball's initial position is relative to the camera's position
    private static final Vector DISPLACEMENT = new Vector(0f, -2f, -2.5f);
    private static final float RADIUS = 1.0f;

    private final float[] viewMatrix = MatrixUtils.newMatrix();
    private final float[] projectionMatrix = MatrixUtils.newMatrix();

    private final Context context;
    private final Vector lightPosition = new Vector(0f, 10f, 0f);

    private Camera camera;

    private BasketBall basketBall;
    private Goal goal;

    private List<Renderable> models;

    private boolean shotInProgress;
    private boolean shotFinished;

    private boolean replayInProgress;
    private boolean gameOver;

    private ShotListener shotListener;
    private ReplayListener replayListener;

    private Plane floor;

    public BasketBallRenderer(Context context, Camera camera) {
        Log.d(TAG, "Instantiating renderer");

        this.context = context;
        this.camera = camera;

        this.models = new ArrayList<Renderable>();

        Vector floorNormal = new Vector(0, 1, 0);
        Vector pointOnFloor = new Vector(0, 0, 0);

        this.floor = new Plane(floorNormal, pointOnFloor);
    }

    public boolean isReplayInProgress() {
        return replayInProgress;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setShotListener(ShotListener shotListener) {
        this.shotListener = shotListener;
    }

    public void setReplayListener(ReplayListener replayListener) {
        this.replayListener = replayListener;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "Creating surface");

        glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        if(models.isEmpty())
            createModels();
    }

    private void createModels() {
        basketBall = createBasketBall();
        models.add(basketBall);

        goal = GoalFactory.create(context);
        models.add(goal);
    }

    private BasketBall createBasketBall() {
        Log.d(TAG, "Creating basketball");

        OpenGLProgram program = OpenGLProgramFactory.create(context,
                R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);

        Vector eye = camera.getPosition();
        Vector position = new Vector(eye).add(DISPLACEMENT);

        int texture = TextureUtils.loadTexture(context, R.drawable.basketball_texture);

        return new BasketBall(position, RADIUS, program, texture);
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

        if(collidesWithBackboard()) {
            Log.d(TAG, "Ball hit the backboard");
        }

        if(collidesWithFloor()) {
            Log.d(TAG, "Ball hit the floor");
            setShotCompleteFlags();
            checkForGameOver();
        }
    }

    private boolean collidesWithBackboard() {
//        Plane backboard = goal.getBackboardPlane();
//        return collidesWithPlane(backboard);

        Sphere ball = basketBall.getSphere();
        BoundingBox boundingBox = goal.getBackboardBoundingBox();

        return CollisionDetector.intersectsWith(boundingBox, ball);
    }

    private boolean collidesWithFloor() {
        return collidesWithPlane(floor);
    }

    private boolean collidesWithPlane(Plane plane) {
        Sphere ball = basketBall.getSphere();
        CollisionResult result = checkSpherePlaneCollision(ball, plane);
        return result.equals(CollisionResult.INTERSECTS);
    }

    private void setShotCompleteFlags() {
        shotInProgress = false;
        shotFinished = true;
    }

    private void checkForGameOver() {
        Log.d(TAG, "Checking for game over conditions");

        if(replayInProgress) {
            stopReplayListener();
            setGameCompleteFlags();
        }
        else {
            completeShotListener();
        }
    }

    private void stopReplayListener() {
        if(replayListener != null)
            replayListener.onStop();
    }

    private void completeShotListener() {
        if(shotListener != null)
            shotListener.onComplete();
    }

    private void setGameCompleteFlags() {
        Log.d(TAG, "Game complete");
        replayInProgress = false;
        gameOver = true;
    }

    public void startReplay() {
        if(!gameOver) {
            Log.d(TAG, "Preparing for replay shot");
            setReplayFlags();
            basketBall.resetTime();
            startReplayListener();
        }
    }

    private void startReplayListener() {
        if(replayListener != null)
            replayListener.onStart();
    }

    private void setReplayFlags() {
        shotFinished = false;
        shotInProgress = true;
        replayInProgress = true;
        gameOver = false;
    }

    public boolean canShoot() {
        return !shotFinished && !shotInProgress && !replayInProgress && !gameOver;
    }

    public void shootBall(float speed) {
        shotInProgress = true;

        Vector direction = new Vector(camera.getDirection());
        basketBall.setDirection(direction, speed);

        startShotListener();
    }

    private void startShotListener() {
        if(shotListener != null)
            shotListener.onStart();
    }

    public void changeBallUpdateSpeed(float factor) {
        basketBall.changeTimeFactor(factor);
    }
}
