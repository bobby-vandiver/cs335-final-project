uniform mat4 modelViewProjection;
uniform mat4 modelView;

attribute vec4 position;
attribute vec3 normal;
attribute vec2 textureCoordinates;

varying vec3 surfaceNormal;

varying vec3 interpolatedPosition;
varying vec2 interpolatedTextureCoordinates;

void main() {

    interpolatedPosition = vec3(modelView * position);
    interpolatedTextureCoordinates = textureCoordinates;

    surfaceNormal = vec3(modelView * vec4(normal, 0.0));
    gl_Position = modelViewProjection * position;
}