package edu.uky.cs335final.basketball.geometry;

public class Sphere {

    private Point center;
    private float radius;

    public Sphere(Point center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public Point getCenter() {
        return center;
    }

    public float getRadius() {
        return radius;
    }
}
