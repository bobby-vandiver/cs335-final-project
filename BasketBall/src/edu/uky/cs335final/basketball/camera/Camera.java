package edu.uky.cs335final.basketball.camera;

import android.util.Log;
import edu.uky.cs335final.basketball.geometry.Vector;

import static edu.uky.cs335final.basketball.matrix.MatrixUtils.*;
import static android.opengl.Matrix.*;

public class Camera {

    private static final String TAG = Camera.class.getCanonicalName();

    private Vector position;
    private Vector direction;
    private Vector up;

    public Camera(Vector position, Vector direction, Vector up) {
        this.position = position;
        this.direction = direction.normalize();
        this.up = up.normalize();

        Log.d(TAG, "position [" + this.position + "]");
        Log.d(TAG, "direction [" + this.direction + "]");
        Log.d(TAG, "up [" + this.up + "]");
    }

    public Vector getPosition() {
        return position;
    }

    public Vector getDirection() {
        return direction;
    }

    public void createViewMatrix(float[] viewMatrix) {
        Vector center = new Vector(position).add(direction);
        setLookAtM(viewMatrix, 0, position.x, position.y, position.z, center.x, center.y, center.z, up.x, up.y, up.z);
    }

    public void updatePosition(Vector displacement) {
        Log.d(TAG, "Updating position [" + displacement + "]");
        position.add(displacement);
    }

    public void updateYaw(float angle) {
        Log.d(TAG, "Updating yaw [" + angle + "]");

        final float[] yawRotation = rotate(angle, up.x, up.y, up.z);
        direction = multiply(yawRotation, direction).normalize();
    }

    public void updatePitch(float angle) {
        Log.d(TAG, "Updating pitch [" + angle + "]");

        Vector side = getSide();
        final float[] pitchRotation = rotate(angle, side.x, side.y, side.z);

        direction = multiply(pitchRotation, direction).normalize();
        up = multiply(pitchRotation, up).normalize();
    }

    public void updateRoll(float angle) {
        Log.d(TAG, "Updating roll [" + angle + "]");

        final float[] rollRotation = rotate(angle, direction.x, direction.y, direction.z);
        up = multiply(rollRotation, up).normalize();
    }

    private Vector getSide() {
        return direction.cross(up).normalize();
    }
}
