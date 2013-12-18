package edu.uky.cs335final.basketball.geometry;

public class UnitCuboid {

    // vertex coords array for glDrawArrays() =====================================
    // A cube has 6 sides and each side has 2 triangles, therefore, a cube consists
    // of 36 vertices (6 sides * 2 tris * 3 vertices = 36 vertices). And, each
    // vertex is 3 components (x,y,z) of floats, therefore, the size of vertex
    // array is 108 floats (36 * 3 = 108).
    public static final float[] VERTICES = {
            1, 1, 1,  -1, 1, 1,  -1,-1, 1,      // v0-v1-v2 (front)
            -1,-1, 1,   1,-1, 1,   1, 1, 1,      // v2-v3-v0

            1, 1, 1,   1,-1, 1,   1,-1,-1,       // v0-v3-v4 (right)
            1,-1,-1,   1, 1,-1,   1, 1, 1,       // v4-v5-v0

            1, 1, 1,   1, 1,-1,  -1, 1,-1,      // v0-v5-v6 (top)
            -1, 1,-1,  -1, 1, 1,   1, 1, 1,      // v6-v1-v0

            -1, 1, 1,  -1, 1,-1,  -1,-1,-1,      // v1-v6-v7 (left)
            -1,-1,-1,  -1,-1, 1,  -1, 1, 1,      // v7-v2-v1

            -1,-1,-1,   1,-1,-1,   1,-1, 1,      // v7-v4-v3 (bottom)
            1,-1, 1,  -1,-1, 1,  -1,-1,-1,      // v3-v2-v7

            1,-1,-1,  -1,-1,-1,  -1, 1,-1,      // v4-v7-v6 (back)
            -1, 1,-1,   1, 1,-1,   1,-1,-1       // v6-v5-v4
    };

    public static final float[] NORMALS  = {
            0, 0, 1,   0, 0, 1,   0, 0, 1,      // v0-v1-v2 (front)
            0, 0, 1,   0, 0, 1,   0, 0, 1,      // v2-v3-v0

            1, 0, 0,   1, 0, 0,   1, 0, 0,      // v0-v3-v4 (right)
            1, 0, 0,   1, 0, 0,   1, 0, 0,      // v4-v5-v0

            0, 1, 0,   0, 1, 0,   0, 1, 0,      // v0-v5-v6 (top)
            0, 1, 0,   0, 1, 0,   0, 1, 0,      // v6-v1-v0

            -1, 0, 0,  -1, 0, 0,  -1, 0, 0,     // v1-v6-v7 (left)
            -1, 0, 0,  -1, 0, 0,  -1, 0, 0,     // v7-v2-v1

            0,-1, 0,   0,-1, 0,   0,-1, 0,      // v7-v4-v3 (bottom)
            0,-1, 0,   0,-1, 0,   0,-1, 0,      // v3-v2-v7

            0, 0,-1,   0, 0,-1,   0, 0,-1,      // v4-v7-v6 (back)
            0, 0,-1,   0, 0,-1,   0, 0,-1       // v6-v5-v4
    };

}
