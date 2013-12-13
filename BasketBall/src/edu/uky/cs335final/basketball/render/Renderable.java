package edu.uky.cs335final.basketball.render;

import edu.uky.cs335final.basketball.geometry.Vector;

public interface Renderable {

    /**
     * Provides models a way to customize how they are rendered.
     *
     * Implementing classes MUST handle the actual rendering, by
     * invoking the relevant OpenGL methods.
     *
     * @param viewMatrix
     * @param projectionMatrix
     */
    public void render(float[] viewMatrix, float[] projectionMatrix, Vector lightPosition);
}
