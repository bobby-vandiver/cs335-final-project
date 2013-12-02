package edu.uky.cs335final.basketball.matrix;

public class MatrixBuilder {

    private float[] matrix;

    public MatrixBuilder() {
        matrix = MatrixUtils.newMatrix();
    }

    public MatrixBuilder translate(float x, float y, float z) {
        final float[] translate = MatrixUtils.translate(x, y, z);
        matrix = MatrixUtils.multiply(translate, matrix);
        return this;
    }

    public MatrixBuilder scale(float x, float y, float z) {
        final float[] scale = MatrixUtils.scale(x, y, z);
        matrix = MatrixUtils.multiply(scale, matrix);
        return this;
    }

    public MatrixBuilder multiply(float[] lhs) {
        matrix = MatrixUtils.multiply(lhs, matrix);
        return this;
    }

    public float[] build() {
        return matrix;
    }
}
