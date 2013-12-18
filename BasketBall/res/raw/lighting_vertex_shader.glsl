uniform mat4 modelViewProjection;
uniform mat4 modelView;
uniform mat4 normalTransformation;

attribute vec4 position;
attribute vec3 normal;
attribute vec2 textureCoordinates;

varying vec3 surfaceNormal;

varying vec3 interpolatedPosition;
varying vec2 interpolatedTextureCoordinates;

void main() {

    interpolatedTextureCoordinates = textureCoordinates;

    vec4 temp = normalTransformation * vec4(normal, 0.0);
    surfaceNormal.xyz = temp.xyz;

    temp = modelView * position;
    interpolatedPosition.xyz = temp.xyz;

    gl_Position = modelViewProjection * position;
}