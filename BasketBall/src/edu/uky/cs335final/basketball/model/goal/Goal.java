package edu.uky.cs335final.basketball.model.goal;

import android.util.Log;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.render.Renderable;
import edu.uky.cs335final.basketball.shader.OpenGLProgram;

public class Goal implements Renderable {

    private static final String TAG = Goal.class.getCanonicalName();

    private Backboard backboard;

    public Goal(OpenGLProgram program, int backboardTexture) {
        Vector position = new Vector(0f, 10f, 0f);
        backboard = new Backboard(position, program, backboardTexture);
    }

    @Override
    public void render(float[] viewMatrix, float[] projectionMatrix, Vector lightPosition) {

        Log.v(TAG, "Rendering backboard");
        backboard.render(viewMatrix, projectionMatrix, lightPosition);
    }
}
