package edu.uky.cs335final.basketball.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import edu.uky.cs335final.basketball.camera.Camera;
import edu.uky.cs335final.basketball.render.BasketBallRenderer;

public class BasketBallView extends GLSurfaceView {

    private static final String TAG = BasketBallView.class.getCanonicalName();
    private static final int OPENGL_ES_VERSION = 2;

    private BasketBallRenderer renderer;

    public BasketBallView(Context context, Camera camera) {
        super(context);
        this.renderer = new BasketBallRenderer(context, camera);

        setEGLContextClientVersion(OPENGL_ES_VERSION);
        setRenderer(renderer);
    }

    public void shootBall(final float power) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Queueing shoot event");
                if(renderer.canShoot())
                    renderer.shootBall(power);
            }
        });
    }
}
