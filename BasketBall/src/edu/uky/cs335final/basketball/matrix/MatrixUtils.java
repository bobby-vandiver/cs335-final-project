package edu.uky.cs335final.basketball.matrix;

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

    public static float[] multiply(float[] leftMatrix, float[] rightMatrix) {
        throwIfInvalidSize(leftMatrix);
        throwIfInvalidSize(rightMatrix);

        float[] result = newMatrix();
        multiplyMM(result, 0, leftMatrix, 0, rightMatrix, 0);
        return result;
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
