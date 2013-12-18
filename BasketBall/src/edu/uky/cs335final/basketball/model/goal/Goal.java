package edu.uky.cs335final.basketball.model.goal;

import android.util.Log;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.render.Renderable;

public class Goal implements Renderable {

    private static final String TAG = Goal.class.getCanonicalName();

    private Pole pole;
    private Backboard backboard;


    public Goal(Backboard backboard, Pole pole) {
        this.backboard = backboard;
        this.pole = pole;
    }

    @Override
    public void render(float[] viewMatrix, float[] projectionMatrix, Vector lightPosition) {

        Log.v(TAG, "Rendering pole");
        pole.render(viewMatrix, projectionMatrix, lightPosition);

        Log.v(TAG, "Rendering backboard");
        backboard.render(viewMatrix, projectionMatrix, lightPosition);
    }
}
