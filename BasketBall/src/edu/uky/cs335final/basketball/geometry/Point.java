package edu.uky.cs335final.basketball.geometry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

// Simple data structure to represent a point in space
public class Point {

    public static final int COMPONENT_SIZE = 4;
    public static final int COMPONENTS_PER_POINT = 3;

    public static Point ORIGIN = new Point(0f, 0f, 0f);

    public float x;
    public float y;
    public float z;

    public Point(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z +")";
    }

    public static FloatBuffer pointsToFloatBuffer(Point[] points) {
        final int bufferSize = points.length * COMPONENT_SIZE * COMPONENTS_PER_POINT;

        FloatBuffer buffer =  ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();

        float coordinates[] = pointsToFloatArray(points);

        buffer.put(coordinates);
        buffer.position(0);

        return buffer;
    }

    private static float[] pointsToFloatArray(Point[] points) {
        final int floatCount = points.length * COMPONENTS_PER_POINT;
        float coordinates[] = new float[floatCount];

        for(int i = 0; i < points.length; i++) {
            int idx = i * COMPONENTS_PER_POINT;

            coordinates[idx] = points[i].x;
            coordinates[idx + 1] = points[i].y;
            coordinates[idx + 2] = points[i].z;
        }

        return coordinates;
    }
}
