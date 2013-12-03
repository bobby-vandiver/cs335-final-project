package edu.uky.cs335final.basketball;

import edu.uky.cs335final.basketball.geometry.Vector;

import static android.opengl.Matrix.*;

public class Camera {

    private static final String TAG = Camera.class.getCanonicalName();

    private Vector eye;
    private Vector center;
    private Vector up;

    public Camera(Vector eye, Vector center, Vector up) {
        this.eye = eye;
        this.center = center;
        this.up = up;
    }

    public void createViewMatrix(float[] viewMatrix) {
        setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z);
    }
}
