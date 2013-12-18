package edu.uky.cs335final.basketball.model.goal;

import android.content.Context;
import android.util.Log;
import edu.uky.cs335final.basketball.R;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.shader.OpenGLProgram;
import edu.uky.cs335final.basketball.shader.OpenGLProgramFactory;
import edu.uky.cs335final.basketball.shader.TextureUtils;

public class GoalFactory {

    private static final String TAG = GoalFactory.class.getCanonicalName();

    // Position of pole and hoop defined relative to backboard
    private static final Vector POLE_DISPLACEMENT = new Vector(0f, -5f, -2f);
    private static final Vector HOOP_DISPLACEMENT = new Vector(0f, -2f, 3f);

    private static final Vector BACKBOARD_POSITION = new Vector(0f, 10f, 0f);

    public static Goal create(Context context) {
        Log.d(TAG, "Creating goal");

        Backboard backboard = createBackboard(context);
        Pole pole = createPole(context);
        Hoop hoop = createHoop(context);

        return new Goal(backboard, pole, hoop);
    }

    private static Backboard createBackboard(Context context) {
        OpenGLProgram program = OpenGLProgramFactory.create(context,
                R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);

        int texture = TextureUtils.loadTexture(context, R.drawable.backboard_texture);
        return new Backboard(BACKBOARD_POSITION, program, texture);
    }

    private static Pole createPole(Context context) {
        OpenGLProgram program = OpenGLProgramFactory.create(context,
                R.raw.lighting_vertex_shader, R.raw.lighting_fragment_shader);

        Vector position = new Vector(BACKBOARD_POSITION).add(POLE_DISPLACEMENT);
        return new Pole(position, program);
    }

    private static Hoop createHoop(Context context) {
        OpenGLProgram program = OpenGLProgramFactory.create(context,
                R.raw.lighting_vertex_shader, R.raw.lighting_fragment_shader);

        Vector position = new Vector(BACKBOARD_POSITION).add(HOOP_DISPLACEMENT);
        return new Hoop(position, program);
    }
}
