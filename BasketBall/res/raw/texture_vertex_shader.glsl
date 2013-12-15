uniform mat4 modelViewProjection;

attribute vec4 position;
attribute vec2 textureCoordinates;

varying vec2 interpolatedTextureCoordinates;

void main() {
    interpolatedTextureCoordinates = textureCoordinates;
    gl_Position = modelViewProjection * position;
}