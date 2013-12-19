package edu.uky.cs335final.basketball.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Toast;
import edu.uky.cs335final.basketball.camera.Camera;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.render.BasketBallRenderer;

public class BasketBallView extends GLSurfaceView {

    private static final String TAG = BasketBallView.class.getCanonicalName();
    private static final int OPENGL_ES_VERSION = 2;

    private static final float SCALE_MODIFIER = 0.25f;
    private static final float POWER_MODIFIER = 1.5f;

    private BasketBallRenderer renderer;
    private ScaleGestureDetector scaleGestureDetector;

    public BasketBallView(Context context, BasketBallRenderer renderer) {
        super(context);
        this.renderer = renderer;

        setEGLContextClientVersion(OPENGL_ES_VERSION);
        setRenderer(renderer);
    }

    public void setUpScaleGestureDetector(Context context, final Camera camera) {

        final ScaleGestureDetector.OnScaleGestureListener listener = new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {

                float factor = calculateMovementFactor(detector);
                Log.d(TAG, "Moving the camera based on scale factor [" + factor + "]");

                // Move forward or backward based on the direction the camera is facing
                Vector displacement = new Vector(camera.getDirection()).multiply(factor);
                camera.updatePosition(displacement);

                invalidate();
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        };

        this.scaleGestureDetector = new ScaleGestureDetector(context, listener);
    }

    private float calculateMovementFactor(ScaleGestureDetector detector) {
        float factor = detector.getScaleFactor();

        if(isMovingBackward(factor))
            factor *= -1f;

        return factor * SCALE_MODIFIER;
    }

    private boolean isMovingBackward(final float factor) {
        int comparison = Float.compare(factor, 1.0f);
        return comparison < 0;
    }

    public void disableScaleGestureDetector() {
        scaleGestureDetector = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return scaleGestureDetector != null && scaleGestureDetector.onTouchEvent(event);
    }

    public void shootBall(final float power) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Queueing shoot event");
                if(renderer.canShoot())
                    renderer.shootBall(power * POWER_MODIFIER);
            }
        });
    }
}
