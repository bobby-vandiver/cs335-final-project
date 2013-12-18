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

    public MatrixBuilder rotate(float angle, float x, float y, float z) {
        final float[] rotate = MatrixUtils.rotate(angle, x, y, z);
        matrix = MatrixUtils.multiply(rotate, matrix);
        return this;
    }

    public MatrixBuilder invert() {
        matrix = MatrixUtils.invert(matrix);
        return this;
    }

    public MatrixBuilder transpose() {
        matrix = MatrixUtils.transpose(matrix);
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
