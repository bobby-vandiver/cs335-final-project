package edu.uky.cs335final.basketball.collision;

import edu.uky.cs335final.basketball.geometry.Vector;

public class CollisionDetector {

    public enum SpherePosition { INTERSECTS, IN_FRONT, BEHIND }

    // Adapted from: http://www.opengl.org/discussion_boards/showthread.php/140321-spheres-plane-intersection
    public static SpherePosition checkSpherePlaneCollision(Vector sphereCenter, float radius, Vector planeNormal, Vector pointOnPlane) {


        return SpherePosition.INTERSECTS;
    }



}
