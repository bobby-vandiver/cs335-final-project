uniform mat4 modelViewProjection;
attribute vec4 position;

void main() {
    gl_Position = modelViewProjection * position;
}