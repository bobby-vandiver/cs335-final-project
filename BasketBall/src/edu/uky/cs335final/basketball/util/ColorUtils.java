package edu.uky.cs335final.basketball.util;

public class ColorUtils {

    // R, G, B, A
    private static final int COMPONENT_COUNT = 4;
    private static final float OPAQUE = 1.0f;

    public static float[] fromHexCode(String hexCode) {
        int parsedColor = android.graphics.Color.parseColor(hexCode);

        float[] color = new float[COMPONENT_COUNT];

        color[0] = android.graphics.Color.red(parsedColor) / 255.0f;
        color[1] = android.graphics.Color.green(parsedColor) / 255.0f;
        color[2] = android.graphics.Color.blue(parsedColor) / 255.0f;
        color[3] = OPAQUE;

        return color;
    }
}
