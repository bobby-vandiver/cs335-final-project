precision mediump float;

uniform sampler2D textureUnit;
uniform sampler2D bumpMapUnit;

varying vec2 interpolatedTextureCoordinates;

void main() {
    gl_FragColor = texture2D(textureUnit, interpolatedTextureCoordinates);
}