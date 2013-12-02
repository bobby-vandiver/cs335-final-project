package edu.uky.cs335final.basketball;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class BasketBallView extends GLSurfaceView {

    private static final int OPENGL_ES_VERSION = 2;

    private BasketBallRenderer renderer;

    public BasketBallView(Context context) {
        super(context);
        this.renderer = new BasketBallRenderer();

        setEGLContextClientVersion(OPENGL_ES_VERSION);
        setRenderer(renderer);
    }
}
