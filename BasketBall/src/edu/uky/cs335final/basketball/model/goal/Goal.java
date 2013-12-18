package edu.uky.cs335final.basketball.model.goal;

import android.util.Log;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.render.Renderable;

public class Goal implements Renderable {

    private static final String TAG = Goal.class.getCanonicalName();

    private Backboard backboard;

    private Pole pole;
    private Hoop hoop;

    public Goal(Backboard backboard, Pole pole, Hoop hoop) {
        this.backboard = backboard;
        this.pole = pole;
        this.hoop = hoop;
    }

    @Override
    public void render(float[] viewMatrix, float[] projectionMatrix, Vector lightPosition) {

        Log.v(TAG, "Rendering pole");
        pole.render(viewMatrix, projectionMatrix, lightPosition);

        Log.v(TAG, "Rendering backboard");
        backboard.render(viewMatrix, projectionMatrix, lightPosition);

        Log.v(TAG, "Rendering hoop");
        hoop.render(viewMatrix, projectionMatrix, lightPosition);
    }
}
