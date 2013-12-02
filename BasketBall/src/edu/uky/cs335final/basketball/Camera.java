package edu.uky.cs335final.basketball;

import android.util.Log;
import edu.uky.cs335final.basketball.geometry.Point;

import static android.opengl.Matrix.*;

public class Camera {

    private static final String TAG = Camera.class.getCanonicalName();

    private Point eye;
    private Point center;
    private Point up;

    public Camera(Point eye, Point center, Point up) {
        this.eye = eye;
        this.center = center;
        this.up = up;
    }

    public void createViewMatrix(float[] viewMatrix) {
        setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z);
    }
}
