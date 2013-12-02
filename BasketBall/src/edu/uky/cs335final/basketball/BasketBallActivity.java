package edu.uky.cs335final.basketball;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class BasketBallActivity extends Activity {

    private GLSurfaceView basketBallView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        basketBallView = new BasketBallView(this);
        setContentView(basketBallView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        basketBallView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        basketBallView.onResume();
    }
}
