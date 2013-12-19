package edu.uky.cs335final.basketball.collision;

import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.util.MathUtils;

public class CollisionDetector {

    public enum SpherePosition { INTERSECTS, IN_FRONT, BEHIND }

    // Adapted from: http://www.opengl.org/discussion_boards/showthread.php/140321-spheres-plane-intersection
    public static SpherePosition checkSpherePlaneCollision(Vector sphereCenter, float radius, Vector planeNormal, Vector pointOnPlane) {

        float planeDistanceFromOrigin = MathUtils.distance(planeNormal, pointOnPlane);
        float spherePlaneDistance = Vector.dot(planeNormal, sphereCenter) + planeDistanceFromOrigin;

        boolean intersects = intersects(spherePlaneDistance, radius);
        boolean inFront = inFront(spherePlaneDistance, radius);

        if(intersects) {
            return SpherePosition.INTERSECTS;
        }
        else if(inFront) {
            return SpherePosition.IN_FRONT;
        }
        else {
            return SpherePosition.BEHIND;
        }
    }

    private static boolean intersects(float distance, float radius) {
        return Math.abs(distance) < radius;
    }

    private static boolean inFront(float distance, float radius) {
        return distance >= radius;
    }
}
