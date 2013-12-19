package edu.uky.cs335final.basketball.collision;

import edu.uky.cs335final.basketball.geometry.Vector;

public class BoundingBox {

    private Vector minimum;
    private Vector maximum;

    public BoundingBox(Vector center, float x, float y, float z) {

        float distanceX = x / 2;
        float distanceY = y / 2;
        float distanceZ = z / 2;

        float minX = center.x - distanceX;
        float minY = center.y - distanceY;
        float minZ = center.z - distanceZ;

        minimum = new Vector(minX, minY, minZ);

        float maxX = center.x + distanceX;
        float maxY = center.y + distanceY;
        float maxZ = center.z + distanceZ;

        maximum = new Vector(maxX, maxY, maxZ);
    }

    public Vector getMinimum() {
        return minimum;
    }

    public Vector getMaximum() {
        return maximum;
    }
}
