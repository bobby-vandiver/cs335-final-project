package edu.uky.cs335final.basketball.geometry;

public class Plane {

    private Vector normal;
    private Vector point;

    public Plane(Vector normal, Vector point) {
        this.normal = normal;
        this.point = point;
    }

    public Vector getNormal() {
        return normal;
    }

    public Vector getPoint() {
        return point;
    }

    @Override
    public String toString() {
        return "normal: " + normal + " -- point: " + point;
    }
}
