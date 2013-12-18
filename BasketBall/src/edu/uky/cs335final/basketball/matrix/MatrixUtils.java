package edu.uky.cs335final.basketball.matrix;

import edu.uky.cs335final.basketball.geometry.Vector;

import static android.opengl.Matrix.*;

public class MatrixUtils {

    private static final int MATRIX_SIZE = 16;

    public static float[] translate(float x, float y, float z) {
        float[] translation = newMatrix();
        setIdentityM(translation, 0);
        translateM(translation, 0, x, y, z);
        return translation;
    }

    public static float[] scale(float x, float y, float z) {
        float[] scale = newMatrix();
        scaleM(scale, 0, x, y, z);
        return scale;
    }

    public static float[] rotate(float angle, float x, float y, float z) {
        float[] rotate = newMatrix();
        rotateM(rotate, 0, angle, x, y, z);
        return rotate;
    }

    public static float[] invert(float[] matrix) {
        float[] inverted = newMatrix();
        invertM(inverted, 0, matrix, 0);
        return inverted;
    }

    public static float[] transpose(float[] matrix) {
        float[] transposed = newMatrix();
        transposeM(transposed, 0, matrix, 0);
        return transposed;
    }

    public static float[] multiply(float[] leftMatrix, float[] rightMatrix) {
        throwIfInvalidSize(leftMatrix);
        throwIfInvalidSize(rightMatrix);

        float[] result = newMatrix();
        multiplyMM(result, 0, leftMatrix, 0, rightMatrix, 0);
        return result;
    }

    public static Vector multiply(float[] matrix, Vector vector) {
        throwIfInvalidSize(matrix);

        float[] columnVector = vector.asVec4();
        float[] result = new float[Vector.VEC4_SIZE];

        multiplyMV(result, 0, matrix, 0, columnVector, 0);
        return new Vector(result);
    }

    private static void throwIfInvalidSize(float[] matrix) {
        if(matrix.length != MATRIX_SIZE)
            throw new IllegalArgumentException("Matrix must have length of [" + MATRIX_SIZE + "]");
    }

    public static float[] newMatrix() {
        float[] matrix = new float[MATRIX_SIZE];
        setIdentityM(matrix, 0);
        return matrix;
    }
}
