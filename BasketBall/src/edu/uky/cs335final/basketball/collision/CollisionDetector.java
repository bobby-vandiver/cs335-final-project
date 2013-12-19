package edu.uky.cs335final.basketball.collision;

import edu.uky.cs335final.basketball.geometry.Plane;
import edu.uky.cs335final.basketball.geometry.Sphere;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.util.MathUtils;

public class CollisionDetector {

    public enum CollisionResult { INTERSECTS, IN_FRONT, BEHIND }

    // Adapted from: http://www.opengl.org/discussion_boards/showthread.php/140321-spheres-plane-intersection
    public static CollisionResult checkSpherePlaneCollision(Sphere sphere, Plane plane) {

        Vector sphereCenter = sphere.getCenter();
        float radius = sphere.getRadius();

        Vector planeNormal = plane.getNormal();
        Vector pointOnPlane = plane.getPoint();

        float planeDistanceFromOrigin = MathUtils.distance(planeNormal, pointOnPlane);
        float spherePlaneDistance = Vector.dot(planeNormal, sphereCenter) + planeDistanceFromOrigin;

        boolean intersects = intersects(spherePlaneDistance, radius);
        boolean inFront = inFront(spherePlaneDistance, radius);

        if(intersects) {
            return CollisionResult.INTERSECTS;
        }
        else if(inFront) {
            return CollisionResult.IN_FRONT;
        }
        else {
            return CollisionResult.BEHIND;
        }
    }

    private static boolean intersects(float distance, float radius) {
        return Math.abs(distance) < radius;
    }

    private static boolean inFront(float distance, float radius) {
        return distance >= radius;
    }
}
