package edu.uky.cs335final.basketball.collision;

import edu.uky.cs335final.basketball.geometry.Plane;
import edu.uky.cs335final.basketball.geometry.Sphere;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.util.MathUtils;

public class CollisionDetector {

    public enum CollisionResult { INTERSECTS, IN_FRONT, BEHIND }


    // Source:
    // http://stackoverflow.com/questions/15247347/collision-detection-between-a-boundingbox-and-a-sphere-in-libgdx
    public static boolean intersectsWith(BoundingBox boundingBox, Sphere sphere) {
        float dmin = 0;

        Vector center = sphere.getCenter();
        Vector bmin = boundingBox.getMinimum();
        Vector bmax = boundingBox.getMaximum();

        if (center.x < bmin.x) {
            dmin += Math.pow(center.x - bmin.x, 2);
        } else if (center.x > bmax.x) {
            dmin += Math.pow(center.x - bmax.x, 2);
        }

        if (center.y < bmin.y) {
            dmin += Math.pow(center.y - bmin.y, 2);
        } else if (center.y > bmax.y) {
            dmin += Math.pow(center.y - bmax.y, 2);
        }

        if (center.z < bmin.z) {
            dmin += Math.pow(center.z - bmin.z, 2);
        } else if (center.z > bmax.z) {
            dmin += Math.pow(center.z - bmax.z, 2);
        }

        return dmin <= Math.pow(sphere.getRadius(), 2);
    }

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
