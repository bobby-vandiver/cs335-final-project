package edu.uky.cs335final.basketball.util;

import edu.uky.cs335final.basketball.geometry.Vector;

import static android.util.FloatMath.*;

public class MathUtils {

    // Solves for d in the plane equation ax + by + cz + d = 0
    // where the plane normal is represented by the vector <a, b, c>
    // and a point on the plane is represented by the vector <x_0, y_0, z_0>
    //
    // Thus d = -(a * x_0 + b * y_0 + c * z_0)

    public static float getPlaneConstant(Vector normal, Vector point) {

        float x = normal.x * point.x;
        float y = normal.y * point.y;
        float z = normal.z * point.z;

        return -(x + y + z);
    }

    // Calculates the distance from the point to the plane defined by
    // the plane normal and constant, where normal is <a, b, c> and
    // constant is d in the equation ax + by + cz + d = 0

    public static float distance(Vector normal, Vector point) {
        float constant = getPlaneConstant(normal, point);

        float numerator = (normal.x * point.x) + (normal.y * point.y) + (normal.z * point.z) + constant;
        numerator = Math.abs(numerator);

        float denominator = normal.magnitude();
        return numerator / denominator;
    }
}
