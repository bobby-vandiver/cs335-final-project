package edu.uky.cs335final.basketball.geometry;

// Simple data structure to represent a vector in space
public class Vector {

    public static final int COMPONENT_SIZE = 4;
    public static final int COMPONENTS_PER_POINT = 3;

    public static Vector ORIGIN = new Vector(0f, 0f, 0f);

    public float x;
    public float y;
    public float z;

    public Vector() {
        this(0f, 0f, 0f);
    }

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(Vector other) {
        this(other.x, other.y, other.z);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z +")";
    }

    public Vector normalize() {
        return multiply(1 / magnitude());
    }

    public Vector add(Vector other) {
        x += other.x;
        y += other.y;
        z += other.z;
        return this;
    }

    public Vector subtract(Vector subtrahend) {
        x -= subtrahend.x;
        y -= subtrahend.y;
        z -= subtrahend.z;
        return this;
    }

    public Vector multiply(float scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    public float magnitude() {
        return (float) Math.sqrt((x * x) + (y * y) + (z * z));
    }
}
