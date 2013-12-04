package edu.uky.cs335final.basketball;

import android.content.Context;
import android.opengl.GLSurfaceView;
import edu.uky.cs335final.basketball.render.BasketBallRenderer;

public class BasketBallView extends GLSurfaceView {

    private static final int OPENGL_ES_VERSION = 2;

    private BasketBallRenderer renderer;

    public BasketBallView(Context context) {
        super(context);
        this.renderer = new BasketBallRenderer(context);

        setEGLContextClientVersion(OPENGL_ES_VERSION);
        setRenderer(renderer);
    }

    public void shootBall(float power) {
        if(renderer.canShoot())
            renderer.shootBall(power);
    }
}
