package edu.uky.cs335final.basketball.geometry;

public class Cuboid {

    private Vector center;

    // Relative to Y-axis
    private float length;

    // Relative to X-axis
    private float width;

    // Relative to Z-axis
    private float depth;

    public Cuboid(Vector center, float width, float length, float depth) {
        this.center = center;
        this.length = length;
        this.width = width;
        this.depth = depth;
    }

    public Vector getNormal() {
        Vector a = new Vector(width, 0, 0);
        Vector b = new Vector(0, length, 0);
        return a.cross(b).normalize();
    }

    public Vector getCenter() {
        return center;
    }

    public float getLength() {
        return length;
    }

    public float getWidth() {
        return width;
    }

    public float getDepth() {
        return depth;
    }
}
