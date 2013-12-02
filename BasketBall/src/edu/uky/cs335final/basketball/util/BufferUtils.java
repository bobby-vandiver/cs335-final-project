package edu.uky.cs335final.basketball.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class BufferUtils {

    private static final int SHORT_SIZE = 2;
    private static final int FLOAT_SIZE = 4;

    public static ShortBuffer createBuffer(short data[]) {
        final int size = data.length * SHORT_SIZE;

        ShortBuffer buffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asShortBuffer();
        buffer.put(data);
        buffer.position(0);

        return buffer;
    }

    public static FloatBuffer createBuffer(float data[]) {
        final int size = data.length * FLOAT_SIZE;

        FloatBuffer buffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asFloatBuffer();
        buffer.put(data);
        buffer.position(0);

        return buffer;
    }
}
